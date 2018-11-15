package www.ucforward.com.factory;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import www.ucforward.com.entity.ScheduleJob;
import www.ucforward.com.utils.TaskUtils;

public class QuartzJobFactory implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("任务成功运行");
        ScheduleJob scheduleJob = (ScheduleJob)context.getMergedJobDataMap().get("scheduleJob");
        System.out.println("任务名称 = [" + scheduleJob.getJobName() + "]");
        TaskUtils.invokMethod(scheduleJob);
    }
}
