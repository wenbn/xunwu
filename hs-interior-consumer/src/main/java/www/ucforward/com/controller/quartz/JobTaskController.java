package www.ucforward.com.controller.quartz;



import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import www.ucforward.com.entity.ScheduleJob;
import www.ucforward.com.manager.ScheduleJobManager;
import www.ucforward.com.utils.RetObj;
import www.ucforward.com.utils.SpringContextUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

@Controller
@RequestMapping("/task")
public class JobTaskController {
    // 日志记录器
    public final Logger log = Logger.getLogger(this.getClass());

    @Resource
    private ScheduleJobManager scheduleJobManager;

    @RequestMapping("taskList")
    public String taskList(HttpServletRequest request) {
        List<ScheduleJob> taskList = scheduleJobManager.getAllTask();
        request.setAttribute("taskList", taskList);
        return "task/list";
    }

    @RequestMapping("add")
    @ResponseBody
    public RetObj taskList(HttpServletRequest request, ScheduleJob scheduleJob) {
        RetObj retObj = new RetObj();
        retObj.setFlag(false);
        try {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
        } catch (Exception e) {
            retObj.setMsg("cron表达式有误，不能被解析！");
            return retObj;
        }
        Object obj = null;
        try {
            if (StringUtils.isNotBlank(scheduleJob.getSpringId())) {
                obj = SpringContextUtils.getBean(scheduleJob.getSpringId());
            } else {
                Class clazz = Class.forName(scheduleJob.getBeanClass());
                obj = clazz.newInstance();
            }
        } catch (Exception e) {
            // do nothing.........
        }
        if (obj == null) {
            retObj.setMsg("未找到目标类！");
            return retObj;
        } else {
            Class clazz = obj.getClass();
            Method method = null;
            try {
                method = clazz.getMethod(scheduleJob.getMethodName(), null);
            } catch (Exception e) {
                // do nothing.....
            }
            if (method == null) {
                retObj.setMsg("未找到目标方法！");
                return retObj;
            }
        }
        try {
            scheduleJobManager.addTask(scheduleJob);
        } catch (Exception e) {
            e.printStackTrace();
            retObj.setFlag(false);
            retObj.setMsg("保存失败，检查 name group 组合是否有重复！");
            return retObj;
        }

        retObj.setFlag(true);
        return retObj;
    }

    @RequestMapping("changeJobStatus")
    @ResponseBody
    public RetObj changeJobStatus(HttpServletRequest request, Long jobId, String cmd) {
        RetObj retObj = new RetObj();
        retObj.setFlag(false);
        try {
            scheduleJobManager.changeStatus(jobId, cmd);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            retObj.setMsg("任务状态改变失败！");
            return retObj;
        }
        retObj.setFlag(true);
        return retObj;
    }

    @RequestMapping("updateCron")
    @ResponseBody
    public RetObj updateCron(HttpServletRequest request, Long jobId, String cron) {
        RetObj retObj = new RetObj();
        retObj.setFlag(false);
        try {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        } catch (Exception e) {
            retObj.setMsg("cron表达式有误，不能被解析！");
            return retObj;
        }
        try {
            scheduleJobManager.updateCron(jobId, cron);
        } catch (Exception e) {
            retObj.setMsg("cron更新失败！");
            return retObj;
        }
        retObj.setFlag(true);
        return retObj;
    }
}