package www.ucforward.com.dao.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.ScheduleJobDao;
import www.ucforward.com.entity.ScheduleJob;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Repository("scheduleJobDao")
public class ScheduleJobDaoImpl extends SqlSessionDaoSupport implements ScheduleJobDao {


    @PostConstruct
    public void init() throws Exception {
        System.out.println("ScheduleJobDaoImpl ......");
    }

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);

    }

    public List<ScheduleJob> getAll() {
        return this.getSqlSession().selectList("ScheduleJob.getAll");
    }

    public int insertSelective(ScheduleJob job) {
        return this.getSqlSession().insert("ScheduleJob.insertSelective",job);
    }

    public ScheduleJob selectByPrimaryKey(Long jobId) {
        return this.getSqlSession().selectOne("ScheduleJob.selectByPrimaryKey",jobId);

    }

    public void updateByPrimaryKeySelective(ScheduleJob job) {
        this.getSqlSession().update("ScheduleJob.updateByPrimaryKeySelective",job);
    }
}
