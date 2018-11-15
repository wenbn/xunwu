package www.ucforward.com.serviceInter;


import org.springframework.stereotype.Service;
import www.ucforward.com.entity.ScheduleJob;

import java.util.List;

/**
 * Author:wenbn
 * Date:2018/1/3
 * Description:
 */
@Service
public interface JobService {

    /**
     * 从数据库中取 区别于getAllJob
     */
    List<ScheduleJob> getAll();

    /**
     * 添加到数据库中 区别于addJob
     */
    int insertSelective(ScheduleJob job);

    /**
     * 从数据库中查询job
     */
    ScheduleJob selectByPrimaryKey(Long jobId);

    /**
     * 更改任务状态
     * @throws Exception
     */
    void updateByPrimaryKeySelective(ScheduleJob job) throws Exception;
}
