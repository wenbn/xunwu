package www.ucforward.com.manager;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

public class MyJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(new Date() + ": doing something...");
    }

    public void test() throws JobExecutionException {
        System.out.println(" test is : doing something ... ");
    }

    public void test2() throws JobExecutionException {
        System.out.println(" test2 is : doing something ... ");
    }
}
