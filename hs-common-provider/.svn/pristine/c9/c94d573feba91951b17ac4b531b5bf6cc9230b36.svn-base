package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsBoothChannelRelDao;
import www.ucforward.com.entity.HsBoothChannelRel;

import java.util.List;
import java.util.Map;

@Repository("hsBoothChannelRelDao")
public class HsBoothChannelRelDaoImpl extends BaseDao implements HsBoothChannelRelDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsBoothChannelRelDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsBoothChannelRel record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsBoothChannelRel record) {
        return this.getSqlSession().insert("HsBoothChannelRelDao.insertSelective" ,record);
    }

    @Override
    public HsBoothChannelRel selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsBoothChannelRelDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsBoothChannelRel record) {
        return this.getSqlSession().update("HsBoothChannelRelDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsBoothChannelRel record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsBoothChannelRelDao.selectCustomColumnNamesList");
    }

    /**
     * 批量插入数据
     * @param data
     * @param <T>
     * @return
     */
    @Override
    public <T> int batchInsert(List<T> data) {
        return this.getSqlSession().insert("HsBoothChannelRelDao.batchInsert" ,data);
    }

    /**
     * 批量删除数据
     * @param condition
     * @return
     */
    @Override
    public int batchDelete(Map<Object, Object> condition) {
        return this.getSqlSession().delete("HsBoothChannelRelDao.batchDelete" ,condition);
    }
}