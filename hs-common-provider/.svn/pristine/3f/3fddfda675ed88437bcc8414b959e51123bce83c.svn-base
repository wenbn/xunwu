package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsRegionCodeDao;
import www.ucforward.com.entity.HsRegionCode;

import java.util.Map;

@Repository("hsRegionCodeDao")
public class HsRegionCodeDaoImpl extends BaseDao implements HsRegionCodeDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsRegionCodeDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsRegionCode record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsRegionCode record) {
        return this.getSqlSession().insert("HsRegionCodeDao.insertSelective" ,record);
    }

    @Override
    public HsRegionCode selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsRegionCodeDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsRegionCode record) {
        return this.getSqlSession().update("HsRegionCodeDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsRegionCode record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     *
     * @param condition
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return
     */
    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        //默认查询ListMap
        String sql  = "HsRegionCodeDao.selectSupportCityListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsRegionCodeDao.selectSupportCityListByCondition";
        }
        return executeSql(condition,sql);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsRegionCodeDao.selectCustomColumnNamesList");
    }
}