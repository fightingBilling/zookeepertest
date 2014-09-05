package com.wankun.zookeepertest.test2;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

public class FailFirstZooKeeper extends ZooKeeper {
	private boolean isFirst = true;

	public FailFirstZooKeeper(String connectString, int sessionTimeout, Watcher watcher) throws IOException {
		super(connectString, sessionTimeout, watcher);
	}

	/**
	 * 如果是第一次连接，则报连接丢失异常
	 */
	@Override
	public String create(String path, byte data[], List<ACL> acl, CreateMode createMode) throws InterruptedException,
			KeeperException {
		String rc = super.create(path, data, acl, createMode);
		if (isFirst) {
			isFirst = false;
			throw new KeeperException.ConnectionLossException();
		}
		return rc;
	}
}
