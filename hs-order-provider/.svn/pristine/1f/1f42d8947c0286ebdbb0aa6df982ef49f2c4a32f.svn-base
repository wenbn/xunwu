package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHousingOrderRefundApplyDao;
import www.ucforward.com.entity.HsHousingOrderRefundApply;

import java.util.Map;

@Repository("hsHousingOrderRefundApplyDao")
public class HsHousingOrderRefundApplyDaoImpl extends BaseDao implements HsHousingOrderRefundApplyDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHousingOrderRefundApplyDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHousingOrderRefundApply record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHousingOrderRefundApply record) {
        return this.getSqlSession().insert("HsHousingOrderRefundApplyDao.insertSelective",record);
    }

    @Override
    public HsHousingOrderRefundApply selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHousingOrderRefundApplyDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHousingOrderRefundApply record) {
        return this.getSqlSession().update("HsHousingOrderRefundApplyDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHousingOrderRefundApply record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsHousingOrderRefundApplyDao.selectCustomColumnNamesList");
    }

}