package www.ucforward.com.test;

import www.ucforward.com.utils.FastDFSClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程上传
 * @author wenbn
 * @version 1.0
 * @date 2018/7/16
 */
public class FastConcurrence {

    private static int poolSize=6;//定义线程个数
    public static void main(String[] args) throws InterruptedException {
        latchTest();
    }
    private static void latchTest() throws InterruptedException {
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(poolSize);
        ExecutorService exce = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        start.await();
                        testLoad();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        end.countDown();
                    }
                }
            };
            exce.submit(run);
        }
        start.countDown();
        end.await();
        exce.shutdown();
    }

    private static void testLoad() throws Exception {
        File file = new File("D:\\feiji.jpg");
        Map<String,String> metaList = new HashMap<String, String>();
        metaList.put("width","1024");
        metaList.put("height","768");
        metaList.put("author","wenbn");
        metaList.put("date","20161018");
        String fid = FastDFSClient.uploadFile(file,file.getName(),metaList);
        System.out.println("upload local file " + file.getPath() + " ok, fileid=" + fid);
    }

}
