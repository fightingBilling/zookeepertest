package com.wankun.zookeepertest.test3;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;

public class ConfigUpdate {

	public static final String PATH = "/config";
	private ActiveKeyValueStore store;
	private Random random = new Random();

	public ConfigUpdate(String hosts) throws IOException, InterruptedException {
		store = new ActiveKeyValueStore();
		store.connect(hosts);
	}

	/**
	 * 这里不断更新zookeeper中/config 路径下的值
	 * 
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void run() throws KeeperException, InterruptedException {
		while (true) {
			String value = random.nextInt(100) + "";
			store.write(PATH, value);
			System.out.printf("Set %s to %s \n", PATH, value);
			TimeUnit.SECONDS.sleep(random.nextInt(10));
		}
	}

	public static void main(String[] args) throws Exception {
		ConfigUpdate configUpdate = new ConfigUpdate("192.168.204.131");
		configUpdate.run();
	}

}
