package com.wankun.zookeepertest.helloworld;
import java.util.List;

import org.apache.zookeeper.KeeperException;


public class DeleteGroup extends ConnectionWatcher {

	public void delete(String groupname) throws KeeperException, InterruptedException {
		String path = "/" + groupname ;
		try {
			List<String> children = zk.getChildren(path, false);
			for(String child :children)
			{
				zk.delete(path+"/"+child, -1);
			}
			zk.delete(path, -1);
		} catch (KeeperException.NoNodeException e) {
			System.out.printf("Group %s does not exist \n",groupname);
			System.exit(1);
		}
	}

	public static void main(String[] args) throws Exception {
		DeleteGroup deleteGroup = new DeleteGroup();
		deleteGroup.connect("192.168.204.131");
		deleteGroup.delete("zoo");
		deleteGroup.close();
	}

}
