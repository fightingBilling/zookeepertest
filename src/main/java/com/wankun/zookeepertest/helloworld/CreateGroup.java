package com.wankun.zookeepertest.helloworld;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * 连接Zookeeper服务器并创建一个path
 * 
 * @author wankun
 * 
 */
public class CreateGroup extends ConnectionWatcher {

	public void create(String groupName) throws KeeperException, InterruptedException {

		String path = "/" + groupName;
		String createdPath = zk.create(path, null/* no data */, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("Created " + createdPath);
	}

	public static void main(String[] args) throws Exception {
		CreateGroup createGroup = new CreateGroup();
		createGroup.connect("192.168.204.131");
		createGroup.create("zoo");
		createGroup.close();
	}
}
