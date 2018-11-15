package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHousingOrderPaymentOnlineSerialDao;
import www.ucforward.com.entity.HsHousingOrderPaymentOnlineSerial;

@Repository("hsHousingOrderPaymentOnlineSerialDao")
public class HsHousingOrderPaymentOnlineSerialDaoImpl extends BaseDao implements HsHousingOrderPaymentOnlineSerialDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHousingOrderPaymentOnlineSerialDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHousingOrderPaymentOnlineSerial record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHousingOrderPaymentOnlineSerial record) {
        return this.getSqlSession().insert("HsHousingOrderPaymentOnlineSerialDao.insertSelective",record);
    }

    @Override
    public HsHousingOrderPaymentOnlineSerial selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHousingOrderPaymentOnlineSerialDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHousingOrderPaymentOnlineSerial record) {
        return this.getSqlSession().update("HsHousingOrderPaymentOnlineSerialDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHousingOrderPaymentOnlineSerial record) {
        return updateByPrimaryKeySelective(record);
    }
}