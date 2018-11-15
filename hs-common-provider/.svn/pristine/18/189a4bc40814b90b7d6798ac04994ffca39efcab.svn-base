package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsGoldRuleDao;
import www.ucforward.com.entity.HsGoldRule;

import java.util.Map;

@Repository("hsGoldRuleDao")
public class HsGoldRuleDaoImpl extends BaseDao implements HsGoldRuleDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsGoldRuleDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsGoldRule record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsGoldRule record) {
        return this.getSqlSession().insert("HsGoldRuleDao.insertSelective" ,record);
    }

    @Override
    public HsGoldRule selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsGoldRuleDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsGoldRule record) {
        return this.getSqlSession().update("HsGoldRuleDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsGoldRule record) {
        return updateByPrimaryKeySelective(record);
    }


    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsGoldRuleDao.selectCustomColumnNamesList");
    }
}