package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSystemUserOrderTasksLogDao;
import www.ucforward.com.entity.HsSystemUserOrderTasksLog;

import java.util.List;

@Repository("hsSystemUserOrderTasksLogDao")
public class HsSystemUserOrderTasksLogDaoImpl extends BaseDao implements HsSystemUserOrderTasksLogDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsSystemUserOrderTasksLogDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsSystemUserOrderTasksLog record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsSystemUserOrderTasksLog record) {
        return this.getSqlSession().insert("HsSystemUserOrderTasksLogDao.insertSelective" ,record);
    }

    @Override
    public HsSystemUserOrderTasksLog selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsSystemUserOrderTasksLogDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSystemUserOrderTasksLog record) {
        return this.getSqlSession().update("HsSystemUserOrderTasksLogDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsSystemUserOrderTasksLog record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public int batchInsert(List<HsSystemUserOrderTasksLog> logData) {
        return this.getSqlSession().insert("HsSystemUserOrderTasksLogDao.batchInsert" ,logData);
    }
}