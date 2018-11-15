package www.ucforward.com.utils;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/9
 */
public class ZookeeperUtils {
    private static String conneString = "127.0.0.1:2181";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper client = new ZooKeeper(conneString, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {

            }
        });
    }
}
