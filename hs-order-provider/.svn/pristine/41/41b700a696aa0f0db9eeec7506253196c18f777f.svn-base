package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHousingOrderRefundLogDao;
import www.ucforward.com.entity.HsHousingOrderRefundLog;

import java.util.Map;


@Repository("hsHousingOrderRefundLogDao")
public class HsHousingOrderRefundLogDaoImpl extends BaseDao implements HsHousingOrderRefundLogDao {


    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHousingOrderRefundLogDao.deleteByPrimaryKey", id);
    }

    @Override
    public int insert(HsHousingOrderRefundLog record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHousingOrderRefundLog record) {
        return this.getSqlSession().insert("HsHousingOrderRefundLogDao.insertSelective", record);
    }

    @Override
    public HsHousingOrderRefundLog selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHousingOrderRefundLogDao.selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHousingOrderRefundLog record) {
        return this.getSqlSession().update("HsHousingOrderRefundLogDao.updateByPrimaryKeySelective", record);
    }

    @Override
    public int updateByPrimaryKey(HsHousingOrderRefundLog record) {
        return updateByPrimaryKeySelective(record);
    }

}