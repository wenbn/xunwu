package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsBoothDao;
import www.ucforward.com.entity.HsBooth;

import java.util.List;
import java.util.Map;

@Repository("hsBoothDao")
public class HsBoothDaoImpl extends BaseDao implements HsBoothDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsBoothDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsBooth record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsBooth record) {
        return this.getSqlSession().insert("HsBoothDao.insertSelective" ,record);
    }

    @Override
    public HsBooth selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsBoothDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsBooth record) {
        return this.getSqlSession().update("HsBoothDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsBooth record) {
        return updateByPrimaryKeySelective(record);
    }

    //根据频道ids获取所有展位别名
    @Override
    public List<Map<Object, Object>> getBoothsByChannelIds(Map<Object, Object> queryFilter) {
        return this.getSqlSession().selectList("HsBoothDao.getBoothsByChannelIds" ,queryFilter);
    }

    //获取频道下的所有展位别名
    @Override
    public List<Map<Object,Object>> getBoothByChannelAliasName(Map<Object, Object> condition) {
        return this.getSqlSession().selectList("HsBoothDao.getBoothByChannelAliasName" ,condition);
    }

    //查询展位信息
    @Override
    public List<Map<Object, Object>> getBoothsByCondition(Map<Object, Object> condition) {
        return this.getSqlSession().selectList("HsBoothDao.getBoothsByCondition" ,condition);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsBoothDao.selectCustomColumnNamesList");
    }
}