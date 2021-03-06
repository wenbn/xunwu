package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.DictHouseProgressDao;
import www.ucforward.com.entity.DictHouseProgress;

import java.util.List;
import java.util.Map;

@Repository("dictHouseProgressDao")
public class DictHouseProgressDaoImpl extends BaseDao implements DictHouseProgressDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("DictHouseProgressDao.deleteByPrimaryKey", id);
    }

    @Override
    public int insert(DictHouseProgress record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(DictHouseProgress record) {
        return this.getSqlSession().insert("DictHouseProgressDao.insertSelective", record);
    }

    @Override
    public DictHouseProgress selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("DictHouseProgressDao.selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(DictHouseProgress record) {
        return this.getSqlSession().update("DictHouseProgressDao.updateByPrimaryKeySelective", record);
    }

    @Override
    public int updateByPrimaryKey(DictHouseProgress record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public List<Map<String, Object>> findProgressList(String type) {
        return this.getSqlSession().selectList("DictHouseProgressDao.findProgressList",type);
    }

    @Override
    public List<Map<String,Object>> findProgress(Map<Object, Object> condition){
        return this.getSqlSession().selectList("DictHouseProgressDao.findProgress",condition);
    }
}
