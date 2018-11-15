package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHouseCredentialsDataDao;
import www.ucforward.com.entity.HsHouseCredentialsData;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/17.
 */
@Repository("hsHouseCredentialsDataDao")
public class HsHouseCredentialsDataDaoImpl extends BaseDao implements HsHouseCredentialsDataDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHouseCredentialsDataDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHouseCredentialsData record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHouseCredentialsData record) {
        return this.getSqlSession().insert("HsHouseCredentialsDataDao.insertSelective",record);
    }

    @Override
    public HsHouseCredentialsData selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHouseCredentialsDataDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHouseCredentialsData record) {
        return this.getSqlSession().update("HsHouseCredentialsDataDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHouseCredentialsData record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public HsHouseCredentialsData selectCredentialsDataByCondition(Map<Object, Object> condition) {
        return this.getSqlSession().selectOne("HsHouseCredentialsDataDao.selectCredentialsDataByCondition",condition);
    }
    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsHouseCredentialsDataDao.selectCustomColumnNamesList");
    }

    /**
     * 查询列表数据
     * @param condition 查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return Map<Object,Object> result  key data,pageInfo
     * @throws Exception
     */
    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        //默认查询ListMap
        String sql  = "HsHouseCredentialsDataDao.selectHouseCredentialsDataListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsHouseCredentialsDataDao.selectHouseCredentialsDataListByCondition";
        }
        return executeSql(condition, sql);
    }
}
