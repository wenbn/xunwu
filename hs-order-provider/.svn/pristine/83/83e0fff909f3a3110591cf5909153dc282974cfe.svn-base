package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHousingOrderPaymentRecordDao;
import www.ucforward.com.entity.HsHousingOrderPaymentRecord;

@Repository("hsHousingOrderPaymentRecordDao")
public class HsHousingOrderPaymentRecordDaoImpl extends BaseDao implements HsHousingOrderPaymentRecordDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHousingOrderPaymentRecordDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHousingOrderPaymentRecord record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHousingOrderPaymentRecord record) {
        return this.getSqlSession().insert("HsHousingOrderPaymentRecordDao.insertSelective",record);
    }

    @Override
    public HsHousingOrderPaymentRecord selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHousingOrderPaymentRecordDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHousingOrderPaymentRecord record) {
        return this.getSqlSession().update("HsHousingOrderPaymentRecordDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHousingOrderPaymentRecord record) {
        return updateByPrimaryKeySelective(record);
    }
}