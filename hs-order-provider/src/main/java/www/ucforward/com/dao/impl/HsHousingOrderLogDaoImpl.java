package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHousingOrderLogDao;
import www.ucforward.com.entity.HsHousingOrderLog;
import www.ucforward.com.entity.HsSystemOrderPoolLog;

import java.util.List;
import java.util.Map;

@Repository("hsHousingOrderLogDao")
public class HsHousingOrderLogDaoImpl extends BaseDao implements HsHousingOrderLogDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHousingOrderLogDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHousingOrderLog record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHousingOrderLog record) {
        return this.getSqlSession().insert("HsHousingOrderLogDao.insertSelective",record);
    }

    @Override
    public HsHousingOrderLog selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHousingOrderLogDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHousingOrderLog record) {
        return this.getSqlSession().update("HsHousingOrderLogDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHousingOrderLog record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 批量插入数据
     * @param logData
     * @return
     */
    @Override
    public int batchInsert(List<HsHousingOrderLog> logData) {
        return this.getSqlSession().insert("HsHousingOrderLogDao.batchInsert",logData);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsHousingOrderLogDao.selectCustomColumnNamesList");
    }


}