package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Shell;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import util.ConfReader;
import util.common;

public class zooKeeperMonitor implements Watcher, Runnable {

	private ZooKeeper keeper;
	private String connectIp;
	private int sessionTimeout;

	private String hadoopHome;
	private String mapredJobTracker;

	public void initConf() throws IOException {
		ConfReader reader = new ConfReader();
		List<String> keys = new ArrayList<String>();
		keys.add("connectIp");
		keys.add("sessionTimeout");
		keys.add("hadoopHome");
		keys.add("mapred.job.tracker");
		Map<String, String> confs = reader.getConfs(keys);
		this.connectIp = confs.get("connectIp");
		this.sessionTimeout = Integer.parseInt(confs.get("sessionTimeout"));
		this.hadoopHome = confs.get("hadoopHome");
		this.mapredJobTracker = confs.get("mapred.job.tracker");
		ZooKeeper keeper = new ZooKeeper(connectIp, sessionTimeout, this);
	}

	public zooKeeperMonitor() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init() throws IOException, KeeperException,
			InterruptedException {
		SchedulServer schedulServer = new SchedulServer();
		schedulServer.initConf();
		schedulServer.initServer();
		this.initConf();
	}

	@Override
	public void run() {
		zooKeeperMonitor monitor=new zooKeeperMonitor();
		try {
			while (true) {
				monitor.monitorNode();
				Thread.sleep(100000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Thread thread;
		thread = new Thread(new zooKeeperMonitor());
		thread.start();
	}
	@Override
	public void process(WatchedEvent event) {

	}

	public void monitorNode() throws Exception {
		List<String> waits = keeper.getChildren("common.inspence_folder4", false);
		if (waits.isEmpty()) {
			JobID id;
			JobConf jobConf = new JobConf();
			jobConf.set("mapred.job.tracker", this.mapredJobTracker);
			JobClient client = new JobClient(jobConf);
			for (String wait : waits) {
				String data = new String(keeper.getData(common.inspence_folder4
						+ wait, false, null));
				try {
					 id= JobID.forName(wait);
				} catch (Exception e) {
					System.out.println("job id is wrong");
					Stat stat = keeper.exists(common.inspence_folder2 + wait,
							false);
					if (stat != null) {
						keeper.delete(common.inspence_folder2 + wait, -1);
					}
					keeper.delete(common.inspence_folder4 + wait, -1);
					keeper.create(common.inspence_folder2 + wait, data.getBytes(),
							Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					continue;
				}
				int runState=client.getJob((org.apache.hadoop.mapred.JobID)id).getJobState();
				List<String> tempNodes;
				String tempData;
				switch (runState) {
				case JobStatus.RUNNING:
				case JobStatus.PREP:
					break;
				case JobStatus.SUCCEEDED:
					keeper.delete(common.inspence_folder4+wait, -1);
					keeper.create(common.inspence_folder3+wait, data.getBytes(),Ids.OPEN_ACL_UNSAFE , CreateMode.PERSISTENT);
					tempNodes=keeper.getChildren(common.inspence_folder5, false);
					if(tempNodes==null) break;
//					String tempData;
					for(String tempNode : tempNodes){
						tempData=new String(keeper.getData(common.inspence_folder5+tempNode, false, null));
						if(tempData.equals(data)){
							keeper.delete(common.inspence_folder5+tempNode, -1);
						}
					}
					break;
				case JobStatus.FAILED:
				case JobStatus.KILLED:
					keeper.delete(common.inspence_folder4+wait, -1);
					tempNodes=keeper.getChildren(common.inspence_folder5, false);
					keeper.create(common.inspence_folder5+wait, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					if(tempNodes==null && tempNodes.size()==0) {
//						Shell.execCommand(cmd)
						System.out.println("用于shell命令的调用");
//						ShellUtil.callBack(data,hadoopHome);					
					}else{
						Boolean flag=true;
//						String tempData;
						for(String tempNode : tempNodes){
							tempData=new String(keeper.getData(common.inspence_folder5+tempNode, false, null));
							if(tempData.equals(data)){
								keeper.create("common.inspence_folder2"+wait, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
								keeper.delete(common.inspence_folder5+wait, -1);
								keeper.delete(common.inspence_folder5+tempNode, -1);
								flag=false;
							}
						}
//						if(flag)	ShellUtil.callBack(data,hadoopHome);					
						if(flag)	System.out.println("用于shell命令的调用");					
					}
					break;
				default:
					break;
				}
			}
		}

	}

}
