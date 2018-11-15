package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSupportCityDao;
import www.ucforward.com.entity.HsSupportCity;

import java.util.Map;

@Repository("hsSupportCityDao")
public class HsSupportCityDaoImpl extends BaseDao implements HsSupportCityDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsSupportCityDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsSupportCity record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsSupportCity record) {
        return this.getSqlSession().delete("HsSupportCityDao.insertSelective" ,record);
    }

    @Override
    public HsSupportCity selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsSupportCityDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSupportCity record) {
        return this.getSqlSession().update("HsSupportCityDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsSupportCity record) {
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
        String sql  = "HsSupportCityDao.selectSupportCityListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsSupportCityDao.selectSupportCityListByCondition";
        }
        return executeSql(condition,sql);
    }

    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsSupportCityDao.selectCustomColumnNamesList");
    }
}