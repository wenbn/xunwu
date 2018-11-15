package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHouseLogDao;
import www.ucforward.com.dao.HsOwnerHousingApplicationDao;
import www.ucforward.com.entity.HsHouseLog;
import www.ucforward.com.entity.HsOwnerHousingApplication;

import java.util.List;
import java.util.Map;

@Repository("houseLogDao")
public class HsHouseLogDaoImpl extends BaseDao implements HsHouseLogDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHouseLogDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHouseLog record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHouseLog record) {
        return this.getSqlSession().insert("HsHouseLogDao.insertSelective",record);
    }

    @Override
    public HsHouseLog selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHouseLogDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHouseLog record) {
        return this.getSqlSession().update("HsHouseLogDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHouseLog record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        String sql = "HsHouseLogDao.selectHousingStatusListMapByCondition";
        if(returnType != 0){
            sql = "HsHouseLogDao.selectHousingStatusListByCondition";
        }
        return executeSql(condition,sql);
    }
}
