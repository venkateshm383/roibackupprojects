package com.getusroi.zookeeper.staticconfig.session;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.LoggerFactory;

import com.getusroi.zookeeper.staticconfig.service.impl.ZookeeperNodeDataWatcherService;
import com.getusroi.zookeeper.staticconfig.watcher.service.IZookeeperDataWatcherListner;

import ch.qos.logback.classic.Logger;

/**
 * To instantiate watchers and Zookeeper Session..
 * 
 * @author Bizruntime
 *
 */
public class ZookeeperSession implements Watcher, Runnable, IZookeeperDataWatcherListner {

	final CountDownLatch connectedSignal = new CountDownLatch(1);
	static Process child;
	private String[] exec;
	static ZookeeperNodeDataWatcherService zkDwatcher;
	private String filename;
	private static ZooKeeper zk;
	static Properties prop = null;
	static final String ZOOKEEPER_HOST_KEY = "host";
	static final String ZOOKEEPER_TIMEOUT_KEY = "timeout";
	static ZookeeperSession zookeeperSession;
	static String pathToWatch;
	static final String ZOOKEEPER_CONNECTON_PROPS = "zookeeperconnection.properties";
	private static Logger logger = (Logger) LoggerFactory.getLogger(ZookeeperSession.class.getName());

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public ZookeeperSession(ZooKeeper zk) {
		this.zk = zk;
	}

	public static ZooKeeper getZk() {
		return zk;
	}

	public static String getPathToWatch() {
		return pathToWatch;
	}

	public static void setPathToWatch(String pathToWatch) {
		ZookeeperSession.pathToWatch = pathToWatch;
	}

	static {
		prop = new Properties();
		InputStream input = ZookeeperSession.class.getClassLoader().getResourceAsStream(ZOOKEEPER_CONNECTON_PROPS);
		try {
			prop.load(input);
		} catch (IOException e) {
			logger.debug("Unable to load the properties for zookeeper connection establishment..", e);
		}
	}// .. end of static block which gets the connection initialized

	public ZookeeperSession() {

	}

	/**
	 * single instance getter for the ZookeeperConnection
	 * 
	 * @return
	 * @throws IOException
	 */
	public static ZookeeperSession getZookeeperSession() throws IOException {
		if (zookeeperSession == null) {
			synchronized (ZookeeperSession.class) {
				zk = new ZooKeeper(prop.getProperty(ZOOKEEPER_HOST_KEY),
						Integer.parseInt(prop.getProperty(ZOOKEEPER_TIMEOUT_KEY)), new ZookeeperSession());
				zkDwatcher = new ZookeeperNodeDataWatcherService(zk, getPathToWatch(), null, new ZookeeperSession());
				zookeeperSession = new ZookeeperSession(zk);
			}
		}
		return zookeeperSession;
	}// ..end of the method

	/**
	 * Optional constructor can be used if needed extension of session
	 * 
	 * @param hostPort
	 * @param znode
	 * @param filename
	 * @param exec
	 * @throws KeeperException
	 * @throws IOException
	 */
	public ZookeeperSession(String hostPort, String znode, String filename, String[] exec)
			throws KeeperException, IOException {
		this.filename = filename;
		this.exec = exec;
		if (zk == null) {
			zk = new ZooKeeper(hostPort, 3000, this);
		}
		zkDwatcher = new ZookeeperNodeDataWatcherService(zk, znode, null, this);
	}// ..end of parameterized constructor

	@Override
	public void process(WatchedEvent event) {
		zkDwatcher.process(event);
	}

	/**
	 * When exists then keep listening by initiating the thread runtime
	 */
	@Override
	public void exists(byte[] data) {
		if (data == null) {
			if (child != null) {
				child.destroy();
				try {
					child.waitFor();
				} catch (InterruptedException e) {
					logger.error("Exception in zookeeper session wait..", e);
				}
			}
			child = null;
		}
		try {
			child = Runtime.getRuntime().exec(exec);
		} catch (IOException e) {
			logger.error("Exception in zookeeper session thread execution..", e);
		}
	}// ..end of the method

	@Override
	public void closing(int reasonCode) {
		synchronized (this) {
			notifyAll();
		}
	}// ..end of the method

	@Override
	public void run() {
		try {
			synchronized (this) {
				while (!zkDwatcher.dead) {
					wait();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	/**
	 * compulsory close of the zookeeperSession
	 * 
	 * @throws InterruptedException
	 */
	public void close() throws InterruptedException {
		zk.close();
	}// ..end of the method

}
