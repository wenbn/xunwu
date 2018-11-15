package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsChannelDao;
import www.ucforward.com.entity.HsChannel;

import java.util.List;
import java.util.Map;

@Repository("hsChannelDao")
public class HsChannelDaoImpl extends BaseDao implements HsChannelDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsChannelDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsChannel record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsChannel record) {
        return this.getSqlSession().insert("HsChannelDao.insertSelective" ,record);
    }

    @Override
    public HsChannel selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsChannelDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsChannel record) {
        return this.getSqlSession().update("HsChannelDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsChannel record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public List<Map<Object, Object>> selectChannelsByCondition(Map<Object, Object> condition) {
        return this.getSqlSession().selectList("HsChannelDao.selectChannelsByCondition" ,condition);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsChannelDao.selectCustomColumnNamesList");
    }
}