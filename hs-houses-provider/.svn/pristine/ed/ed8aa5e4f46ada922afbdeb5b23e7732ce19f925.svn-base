package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHouseComplainLogDao;
import www.ucforward.com.entity.HsHouseComplainLog;

/**
 * @Auther: lsq
 * @Date: 2018/10/11 11:23
 * @Description:
 */
@Repository("houseComplainLogDao")
public class HsHouseComplainLogDaoImpl extends BaseDao implements HsHouseComplainLogDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHouseComplainLogDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHouseComplainLog record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHouseComplainLog record) {
        return this.getSqlSession().insert("HsHouseComplainLogDao.insertSelective",record);
    }

    @Override
    public HsHouseComplainLog selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHouseComplainLogDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHouseComplainLog record) {
        return this.getSqlSession().update("HsHouseComplainLogDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHouseComplainLog record) {
        return this.getSqlSession().update("HsHouseComplainLogDao.updateByPrimaryKey",record);
    }
}
