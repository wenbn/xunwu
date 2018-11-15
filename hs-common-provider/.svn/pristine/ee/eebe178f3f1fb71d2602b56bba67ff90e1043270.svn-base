package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsArticleDataDao;
import www.ucforward.com.entity.HsArticleData;

import java.util.List;
import java.util.Map;

@Repository("hsArticleDataDao")
public class HsArticleDataDaoImpl extends BaseDao implements HsArticleDataDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsArticleDataDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsArticleData record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsArticleData record) {
        return this.getSqlSession().insert("HsArticleDataDao.insertSelective" ,record);
    }

    @Override
    public HsArticleData selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsArticleDataDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsArticleData record) {
        return this.getSqlSession().update("HsArticleDataDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsArticleData record) {
        return updateByPrimaryKeySelective(record);
    }

    //查询展位下的文章信息
    @Override
    public List<Map<Object, Object>> selectArticleDataByCondition(Map<Object, Object> condition) {
        return this.getSqlSession().selectList("HsArticleDataDao.selectArticleDataByCondition" ,condition);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsArticleDataDao.selectCustomColumnNamesList");
    }
}