package com.wankun.zookeepertest.helloworld;
import java.util.List;

import org.apache.zookeeper.KeeperException;

public class ListGroup extends ConnectionWatcher {

	public void list(String groupname) throws KeeperException, InterruptedException {
		String path = "/" + groupname ;
		try {
			List<String> children = zk.getChildren(path, false);
			if(children.isEmpty())
			{
				System.out.printf("No members in group %s\n",groupname);
				System.exit(1);
			}
			for(String child :children)
			{
				System.out.println(child);
			}
		} catch (KeeperException.NoNodeException e) {
			System.out.printf("Group %s does not exist \n",groupname);
			System.exit(1);
		}
	}

	public static void main(String[] args) throws Exception {
		ListGroup listGroup = new ListGroup();
		listGroup.connect("192.168.204.131");
		listGroup.list("zoo");
		listGroup.close();
	}

}
