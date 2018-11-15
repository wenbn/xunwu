package www.ucforward.com.dao.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsAdvertDataDao;
import www.ucforward.com.entity.HsAdvertData;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("hsAdvertDataDao")
public class HsAdvertDataDaoImpl extends BaseDao implements HsAdvertDataDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsAdvertDataDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsAdvertData record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsAdvertData record) {
        return this.getSqlSession().insert("HsAdvertDataDao.insertSelective" ,record);
    }

    @Override
    public HsAdvertData selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsAdvertDataDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsAdvertData record) {
        return this.getSqlSession().update("HsAdvertDataDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsAdvertData record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public List<Map<Object, Object>> selectAdvertDataByCondition(Map<Object, Object> condition) {
        return this.getSqlSession().selectList("HsAdvertDataDao.selectAdvertDataByCondition" ,condition);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsAdvertDataDao.selectCustomColumnNamesList");
    }
}