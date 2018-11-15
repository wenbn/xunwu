package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsBoothAdvertRelDao;
import www.ucforward.com.entity.HsBoothAdvertRel;

import java.util.List;
import java.util.Map;

@Repository("hsBoothAdvertRelDao")
public class HsBoothAdvertRelDaoImpl extends BaseDao implements HsBoothAdvertRelDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsBoothAdvertRelDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsBoothAdvertRel record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsBoothAdvertRel record) {
        return this.getSqlSession().insert("HsBoothAdvertRelDao.deleteByPrimaryKey" ,record);
    }

    @Override
    public HsBoothAdvertRel selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsBoothAdvertRelDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsBoothAdvertRel record) {
        return this.getSqlSession().update("HsBoothAdvertRelDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsBoothAdvertRel record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsBoothAdvertRelDao.selectCustomColumnNamesList");
    }

    /**
     * 批量插入数据
     * @param data
     * @param <T>
     * @return
     */
    @Override
    public <T> int batchInsert(List<T> data) {
        return this.getSqlSession().insert("HsBoothAdvertRelDao.batchInsert" ,data);
    }

    /**
     * 批量删除数据
     * @return
     */
    @Override
    public int batchDelete(Map<Object, Object> condition) {
        return this.getSqlSession().delete("HsBoothAdvertRelDao.batchDelete" ,condition);
    }
}