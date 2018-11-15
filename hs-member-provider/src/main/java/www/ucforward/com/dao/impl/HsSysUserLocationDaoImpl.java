package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSysUserLocationDao;
import www.ucforward.com.entity.HsSysUserLocation;

@Repository("hsSysUserLocationDao")
public class HsSysUserLocationDaoImpl extends BaseDao implements HsSysUserLocationDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsSysUserLocationDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsSysUserLocation record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsSysUserLocation record) {
        return this.getSqlSession().delete("HsSysUserLocationDao.insertSelective" ,record);
    }

    @Override
    public HsSysUserLocation selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsSysUserLocationDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSysUserLocation record) {
        return this.getSqlSession().selectOne("HsSysUserLocationDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsSysUserLocation record) {
        return updateByPrimaryKeySelective(record);
    }
}