package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsBoothArticleRelDao;
import www.ucforward.com.entity.HsBoothArticleRel;

import java.util.List;
import java.util.Map;

@Repository("hsBoothArticleRelDao")
public class HsBoothArticleRelDaoImpl extends BaseDao implements HsBoothArticleRelDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsBoothArticleRelDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsBoothArticleRel record) {
        return 0;
    }

    @Override
    public int insertSelective(HsBoothArticleRel record) {
        return this.getSqlSession().insert("HsBoothArticleRelDao.insertSelective" ,record);
    }

    @Override
    public HsBoothArticleRel selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsBoothArticleRelDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsBoothArticleRel record) {
        return this.getSqlSession().update("HsBoothArticleRelDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsBoothArticleRel record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsBoothArticleRelDao.selectCustomColumnNamesList");
    }

    /**
     * 批量插入数据
     * @param data
     * @param <T>
     * @return
     */
    @Override
    public <T> int batchInsert(List<T> data) {
        return this.getSqlSession().insert("HsBoothArticleRelDao.batchInsert" ,data);
    }

    /**
     * 批量删除数据
     */
    @Override
    public int batchDelete(Map<Object, Object> condition) {
        return this.getSqlSession().delete("HsBoothArticleRelDao.batchDelete" ,condition);
    }
}