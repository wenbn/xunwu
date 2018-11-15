package www.ucforward.com.serviceImpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import www.ucforward.com.dao.ScheduleJobDao;
import www.ucforward.com.entity.ScheduleJob;
import www.ucforward.com.serviceInter.JobService;
import www.ucforward.com.utils.SpringContextUtils;

import javax.annotation.Resource;
import java.util.List;


@Service("jobService")
public class ScheduleJobServiceImpl implements JobService {

    public ScheduleJobServiceImpl(){
        System.out.println("ScheduleJobServiceImpl is alive....");
    }

    protected final static Logger logger = Logger.getLogger(ScheduleJobServiceImpl.class);

    @Resource
    private ScheduleJobDao scheduleJobDao;//任务调度总控制器

    public List<ScheduleJob> getAll() {
        return scheduleJobDao.getAll();
    }

    public int insertSelective(ScheduleJob job) {
        return scheduleJobDao.insertSelective(job);
    }

    public ScheduleJob selectByPrimaryKey(Long jobId) {
        return scheduleJobDao.selectByPrimaryKey(jobId);
    }

    public void updateByPrimaryKeySelective(ScheduleJob job) throws Exception {
        scheduleJobDao.updateByPrimaryKeySelective(job);
    }
}
