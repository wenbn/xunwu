package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsOwnerHousingApplicationLogDao;
import www.ucforward.com.entity.HsOwnerHousingApplicationLog;

import java.util.Map;

@Repository("ownerHousingApplyLogDao")
public class HsOwnerHousingApplicationLogDaoImpl extends BaseDao implements HsOwnerHousingApplicationLogDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsOwnerHousingApplicationLogDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsOwnerHousingApplicationLog record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsOwnerHousingApplicationLog record) {
        return this.getSqlSession().insert("HsOwnerHousingApplicationLogDao.insertSelective",record);
    }

    @Override
    public HsOwnerHousingApplicationLog selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsOwnerHousingApplicationLogDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsOwnerHousingApplicationLog record) {
        return this.getSqlSession().update("HsOwnerHousingApplicationLogDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsOwnerHousingApplicationLog record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<Object, Object> selectProgressInfoByCondition(Map<Object, Object> condition) {
        return this.executeSql(condition,"HsOwnerHousingApplicationLogDao.selectProgressInfoByCondition");
    }

    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition,"HsOwnerHousingApplicationLogDao.selectCustomColumnNamesList");
    }
}
