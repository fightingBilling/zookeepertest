package com.wankun.zookeepertest.test3;

import helloworld.ConnectionWatcher;

import java.nio.charset.Charset;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class ActiveKeyValueStore extends ConnectionWatcher {

	private static final Charset CHARSET = Charset.forName("UTF-8");

	/**
	 * 为锟节碉拷写锟斤拷荩锟斤拷锟斤拷诘悴伙拷锟斤拷冢锟斤拷锟斤拷锟斤拷诘锟�
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
	 * 锟斤拷取znode锟斤拷荩锟斤拷锟斤拷诘锟斤拷锟捷变化时锟斤拷锟斤拷锟絯atcher锟斤拷锟斤拷锟絧rocess锟斤拷锟斤拷
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
