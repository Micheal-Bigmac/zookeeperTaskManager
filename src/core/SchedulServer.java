package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import util.ConfReader;
import util.common;
/***
 * 用于初始化和维护系统中的原始节点
 * @author Big mac
 *
 */
public class SchedulServer implements Watcher{
	private String connectIp;
	private int sessionTimeOut;
	private ZooKeeper keeper;
	
	public void initConf() throws IOException{
		ConfReader reader=new ConfReader();
		List<String> keys=new ArrayList<String>();
		keys.add("connectIp");
		keys.add("sessionTimeout");
		Map<String, String> confs = reader.getConfs(keys);
		this.connectIp=confs.get("connectIp");
		this.sessionTimeOut=Integer.parseInt(confs.get("sessionTimeout"));
		keeper=new ZooKeeper(connectIp, sessionTimeOut, this);
	}

	public void initServer() throws KeeperException, InterruptedException{
		Stat stat=keeper.exists(common.inspence_folder1, false);
		if(stat==null){
			//根节点
			keeper.create(common.inspence_folder1, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			//失败的任务存储节点
			createErrorNode();
			// 成功的任务存储节点
			createProcessedNode();
			//等待 及正在处理的任务存储节点
			createWaitNode();
			createTempNode();
		}
		stat=keeper.exists(common.inspence_folder2, false);
		if(stat==null){
			createErrorNode();
		}
		stat=keeper.exists(common.inspence_folder3, false);
		if(stat==null){
			createProcessedNode();
		}
		stat=keeper.exists(common.inspence_folder4, false);
		if(stat==null){
			createWaitNode();
		}
		stat=keeper.exists(common.inspence_folder5, false);
		if(stat==null){
			createTempNode();
		}
	}

	private void createTempNode() throws KeeperException, InterruptedException {
		keeper.create(common.inspence_folder5, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	private void createWaitNode() throws KeeperException, InterruptedException {
		keeper.create(common.inspence_folder4, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	private void createProcessedNode() throws KeeperException,
			InterruptedException {
		keeper.create(common.inspence_folder3, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	private void createErrorNode() throws KeeperException, InterruptedException {
		keeper.create(common.inspence_folder2, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	@Override
	public void process(WatchedEvent event) {
		
	}

}
