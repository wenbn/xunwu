package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHousingOrderRefundDao;
import www.ucforward.com.entity.HsHousingOrderRefund;

import java.util.Map;


@Repository("hsHousingOrderRefundDao")
public class HsHousingOrderRefundDaoImpl extends BaseDao implements HsHousingOrderRefundDao {


    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHousingOrderRefundDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHousingOrderRefund record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHousingOrderRefund record) {
        return this.getSqlSession().insert("HsHousingOrderRefundDao.insertSelective",record);
    }

    @Override
    public HsHousingOrderRefund selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHousingOrderRefundDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHousingOrderRefund record) {
        return this.getSqlSession().update("HsHousingOrderRefundDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHousingOrderRefund record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsHousingOrderRefundDao.selectCustomColumnNamesList");
    }
}