package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsUserGoldLogDao;
import www.ucforward.com.entity.HsUserGoldLog;

import java.util.List;
import java.util.Map;

@Repository("hsUserGoldLogDao")
public class HsUserGoldLogDaoImpl extends BaseDao implements HsUserGoldLogDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsUserGoldLogDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsUserGoldLog record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsUserGoldLog record) {
        return this.getSqlSession().insert("HsUserGoldLogDao.insertSelective" ,record);
    }

    @Override
    public HsUserGoldLog selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsUserGoldLogDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsUserGoldLog record) {
        return this.getSqlSession().update("HsUserGoldLogDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsUserGoldLog record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsUserGoldLogDao.selectCustomColumnNamesList");
    }

    /**
     * 批量插入日志
     * @param data
     * @return
     */
    @Override
    public int batchInsert(List<HsUserGoldLog> data) {
        return this.getSqlSession().insert("HsUserGoldLogDao.batchInsert" ,data);
    }
}