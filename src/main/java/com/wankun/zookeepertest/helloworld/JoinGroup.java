package com.wankun.zookeepertest.helloworld;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

public class JoinGroup extends ConnectionWatcher {

	public void join(String groupname, String memberName) throws KeeperException, InterruptedException {
		String path = "/" + groupname + "/" + memberName;
		String createdPath = zk.create(path, null/* no data */, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.out.println("Created " + createdPath);

	}

	public static void main(String[] args) throws Exception {
		JoinGroup joinGroup = new JoinGroup();
		joinGroup.connect("192.168.204.131");
		joinGroup.join("zoo", "duck");
		joinGroup.join("zoo", "cow");
		joinGroup.join("zoo", "goat");
		
		// Stay alive
		Thread.sleep(Long.MAX_VALUE);
		//joinGroup.close();
		
	}

}
