package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsOrderCommissionRecordDao;
import www.ucforward.com.entity.HsOrderCommissionRecord;

import java.util.Map;

@Repository("hsOrderCommissionRecordDao")
public class HsOrderCommissionRecordDaoImpl extends BaseDao implements HsOrderCommissionRecordDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsOrderCommissionRecordDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsOrderCommissionRecord record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsOrderCommissionRecord record) {
        return this.getSqlSession().insert("HsOrderCommissionRecordDao.insertSelective",record);
    }

    @Override
    public HsOrderCommissionRecord selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsOrderCommissionRecordDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsOrderCommissionRecord record) {
        return this.getSqlSession().update("HsOrderCommissionRecordDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsOrderCommissionRecord record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsOrderCommissionRecordDao.selectCustomColumnNamesList");
    }
}