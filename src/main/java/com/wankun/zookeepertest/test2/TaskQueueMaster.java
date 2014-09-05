package com.wankun.zookeepertest.test2;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class TaskQueueMaster implements Watcher {
	HashSet<String> tasks = new HashSet<String>();
	HashSet<String> workers = new HashSet<String>();
	HashMap<String, String> workerToAssignment = new HashMap<String, String>();
	HashMap<String, String> assignmentToWorker = new HashMap<String, String>();
	AtomicLong eventCounter = new AtomicLong();

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		if (args.length != 1) {
			System.err.println("USAGE: zkhostports");
			System.exit(2);
		}
		String hostPort = args[0];
		TaskQueueMaster tqm = new TaskQueueMaster();
		ZooKeeper zk = new ZooKeeper(hostPort, 10000, tqm);
		tqm.startup(zk);
		tqm.schedule();
	}

	private void schedule() throws InterruptedException {
		while (true) {
			long count = eventCounter.get();
			synchronized (this) {
				HashSet<String> availableWorkers = new HashSet<String>(workers);
				HashSet<String> unassignedTasks = new HashSet<String>(tasks);
				LinkedList<Entry<String, String>> assignmentsToRemove = new LinkedList<Entry<String, String>>();
				// check for dead workers and finished assignments
				for (Entry<String, String> assignment : workerToAssignment.entrySet()) {
					if (!workers.contains(assignment.getKey()) || !tasks.contains(assignment.getValue())) {
						assignmentsToRemove.add(assignment);
					} else {
						availableWorkers.remove(assignment.getKey());
						unassignedTasks.remove(assignment.getValue());
					}
				}

				// we have to delete the dead worker assignments outside the
				// iterator
				// to avoid the concurrent modification exception
				for (Entry<String, String> assignment : assignmentsToRemove) {
					workerToAssignment.remove(assignment.getKey());
					assignmentToWorker.remove(assignment.getValue());
				}
				Iterator<String> workerIterator = availableWorkers.iterator();
				Iterator<String> taskIterator = unassignedTasks.iterator();
				while (workerIterator.hasNext() && taskIterator.hasNext()) {
					String worker = workerIterator.next();
					String task = taskIterator.next();
					workerToAssignment.put(worker, task);
					assignmentToWorker.put(task, worker);
					// try {
					// // TODO: make the assignment to worker
					//
					// } catch (KeeperException e) {
					// // no big deal, we'll clean up the worker later
					// eventCounter.incrementAndGet();
					// }
				}
			}
			synchronized (eventCounter) {
				while (count == eventCounter.get()) {
					eventCounter.wait();
				}
			}
		}
	}

	private void signalEvent() {
		synchronized (eventCounter) {
			eventCounter.incrementAndGet();
			eventCounter.notifyAll();
		}
	}

	ZooKeeper zk;

	/**
	 * 1. 实例化zk对象
	 * 2. 使自身成为一个master
	 * 3.
	 * @param zk
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void startup(ZooKeeper zk) throws KeeperException, InterruptedException {
		this.zk = zk;
		becomeMaster();
		setupAssignments();
		setupTasks();
	}

	synchronized private void setupTasks() {
		try {
			List<String> children = zk.getChildren("/tasks", false);
			for (String child : children) {
				String path = "/tasks/" + child;
				tasks.add(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		refreshTaskList();
	}

	/**
	 * 1. 读取 /assign路径下znode的数据
	 * 2. 维护两个Hashset,内容为path 和znode数据的对应关系
	 * 3. 一个路径代表一个worker,所有workers信息保存在hashset中
	 * 
	 * path : /assign/child 为一个zookeeper中的一个路径
	 * assignment : 为上面路径的data数据
	 */
	synchronized private void setupAssignments() {
		try {
			List<String> children = zk.getChildren("/assign", false);
			for (String child : children) {
				String path = "/assign/" + child;
				try {
					byte assignedBytes[] = zk.getData(path, false, null);
					if (assignedBytes.length > 0) {
						String assignment = new String(assignedBytes);
						workerToAssignment.put(path, assignment);
						assignmentToWorker.put(assignment, path);
					}
					workers.add(path);
				} catch (KeeperException.NoNodeException e) {
					// it's okay, the worker may die, we just move on
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		refreshWorkerList();
	}

	private void refreshTaskList() {
		try {
			refreshList("/tasks", tasks, new Watcher() {
				@Override
				public void process(WatchedEvent arg0) {
					refreshTaskList();
				}
			});
			signalEvent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void refreshWorkerList() {
		try {
			refreshList("/assign", workers, new Watcher() {
				@Override
				public void process(WatchedEvent arg0) {
					refreshWorkerList();
				}
			});
			signalEvent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void refreshList(String path, Set<String> list, Watcher watcher) throws KeeperException,
			InterruptedException {
		// TODO: maintain the Set represented by children of the znode
		// represented by path
	}

	private void becomeMaster() throws KeeperException, InterruptedException {
		// TODO: do lock file based master election
	}

	public void process(WatchedEvent event) {
		System.err.println(event);
	}
}
