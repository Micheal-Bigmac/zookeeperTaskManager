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
 * ���ڳ�ʼ����ά��ϵͳ�е�ԭʼ�ڵ�
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
			//���ڵ�
			keeper.create(common.inspence_folder1, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			//ʧ�ܵ�����洢�ڵ�
			createErrorNode();
			// �ɹ�������洢�ڵ�
			createProcessedNode();
			//�ȴ� �����ڴ��������洢�ڵ�
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
