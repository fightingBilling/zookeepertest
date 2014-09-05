package com.wankun.zookeepertest.test2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class TaskQueueClient implements Watcher {

	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		if (args.length < 3) {
			System.err.println("USAGE: zkhostports cmd [args...]");
			System.exit(2);
		}
		TaskQueueClient tqc = new TaskQueueClient();
		ZooKeeper zk = new ZooKeeper(args[0], 10000, tqc);
		byte cmdBytes[] = stringsToBytes(args, 1, args.length);
		String taskPath = ""; // TODO: change to create a task in ZooKeeper. (use the SEQUENCE flag to get a unique id)
		System.out.println("Submitted as " + taskPath);
		tqc.waitForFinish(zk, taskPath);
		System.out.println("Finished");
		
	}

	/**
	 * 锟斤拷锟酵伙拷锟剿的诧拷锟斤拷锟斤拷byte锟斤拷式写锟诫到Stream锟叫ｏ拷锟斤拷转锟斤拷为锟斤拷锟斤拷
	 * @param args
	 * @param i
	 * @param length
	 * @return
	 * @throws IOException
	 */
	private static byte[] stringsToBytes(String[] args, int i, int length) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while(i < length) {
			baos.write(args[i++].getBytes());
			baos.write('\n');
		}
		return baos.toByteArray();
	}

	private void waitForFinish(ZooKeeper zk, String taskPath) throws InterruptedException, KeeperException {
		// TODO: block until the command has finished running
	}

	public void process(WatchedEvent event) {
		System.err.println(event);
		switch(event.getType()) {
		case None:
			if (event.getState() == KeeperState.Expired) {
				System.exit(1);
			}
			break;
		}
	}
}
