package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsMemberGoldLogDao;
import www.ucforward.com.entity.HsMemberGoldLog;

import java.util.Map;

@Repository("hsMemberGoldLogDao")
public class HsMemberGoldLogDaoImpl extends BaseDao implements HsMemberGoldLogDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsMemberGoldLogDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsMemberGoldLog record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsMemberGoldLog record) {
        return this.getSqlSession().insert("HsMemberGoldLogDao.insertSelective",record);
    }

    @Override
    public HsMemberGoldLog selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsMemberGoldLogDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsMemberGoldLog record) {
        return this.getSqlSession().update("HsMemberGoldLogDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsMemberGoldLog record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsMemberGoldLogDao.selectCustomColumnNamesList");
    }
}