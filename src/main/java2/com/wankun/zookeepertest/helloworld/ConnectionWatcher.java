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

	/* 锟斤拷锟斤创锟斤拷锟届步锟斤拷锟接ｏ拷锟斤拷锟斤拷锟斤拷zookeeper锟侥回碉拷锟斤拷锟截碉拷为锟斤拷Watcher锟斤拷锟斤拷锟絧rocess锟斤拷锟斤拷 */
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
