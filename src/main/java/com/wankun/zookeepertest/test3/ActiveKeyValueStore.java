package com.wankun.zookeepertest.test3;

import java.nio.charset.Charset;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.wankun.zookeepertest.helloworld.ConnectionWatcher;

public class ActiveKeyValueStore extends ConnectionWatcher {

	private static final Charset CHARSET = Charset.forName("UTF-8");

	/**
	 * 为节点写数据，如果节点不存在，创建节点
	 * 
	 * @param path
	 * @param value
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void write(String path, String value) throws KeeperException, InterruptedException {
		Stat stat = zk.exists(path, false);
		if (stat == null)
			zk.create(path, value.getBytes(CHARSET), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		else
			zk.setData(path, value.getBytes(CHARSET), -1);
	}

	/**
	 * 读取znode数据，当节点数据变化时会调用watcher对象的process方法
	 * @param path
	 * @param watcher
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public String read(String path, Watcher watcher) throws KeeperException, InterruptedException {
		byte[] data = zk.getData(path, watcher, null);
		return new String(data, CHARSET);
	}

}
