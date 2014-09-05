package com.wankun.zookeepertest.helloworld;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class ConnectionWatcher implements Watcher {
	private static final int SESSION_TIMEOUT = 5000;
	protected ZooKeeper zk;

	private CountDownLatch connectedSignal = new CountDownLatch(1);

	/* 这里创建异步连接，并接受zookeeper的回调，回调为调Watcher对象的process方法 */
	public void connect(String hosts) throws IOException, InterruptedException {
		zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
		connectedSignal.await();
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected)
			connectedSignal.countDown();
	}

	public void close() throws InterruptedException {
		zk.close();
	}
}
