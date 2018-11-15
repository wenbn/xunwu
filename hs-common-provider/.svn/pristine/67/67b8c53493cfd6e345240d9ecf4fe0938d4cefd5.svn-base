package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsBoothHouseRelDao;
import www.ucforward.com.entity.HsBoothHouseRel;

@Repository("hsBoothHouseRelDao")
public class HsBoothHouseRelDaoImpl extends BaseDao implements HsBoothHouseRelDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsBoothHouseRelDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsBoothHouseRel record) {
        return 0;
    }

    @Override
    public int insertSelective(HsBoothHouseRel record) {
        return this.getSqlSession().insert("HsBoothHouseRelDao.insertSelective" ,record);
    }

    @Override
    public HsBoothHouseRel selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsBoothHouseRelDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsBoothHouseRel record) {
        return this.getSqlSession().update("HsBoothHouseRelDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsBoothHouseRel record) {
        return updateByPrimaryKeySelective(record);
    }
}