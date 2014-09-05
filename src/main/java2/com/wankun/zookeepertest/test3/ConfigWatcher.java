package com.wankun.zookeepertest.test3;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

public class ConfigWatcher implements Watcher {

	private ActiveKeyValueStore store;
	
	public ConfigWatcher(String hosts) throws IOException, InterruptedException
	{
		store = new ActiveKeyValueStore();
		store.connect(hosts);
	}
	
	public void display() throws KeeperException, InterruptedException
	{
		String value = store.read(ConfigUpdate.PATH, this);
		System.out.printf("Read %s as %s \n",ConfigUpdate.PATH,value);
	}
	

	@Override
	public void process(WatchedEvent event) {
		if(event.getType()==EventType.NodeDataChanged){
			try {
				display();
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.err.println("Interrupted.Exiting.");
				Thread.currentThread().interrupt();
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
}
