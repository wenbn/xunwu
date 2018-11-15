package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHouseNewBuildingDao;
import www.ucforward.com.entity.HsHouseNewBuilding;

import java.util.List;
import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/8/30 20:25
 * @Description:
 */
@Repository("houseNewBuildingDao")
public class HsHouseNewBuildingDaoImpl extends BaseDao implements HsHouseNewBuildingDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHouseNewBuildingDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHouseNewBuilding record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHouseNewBuilding record) {
        return this.getSqlSession().insert("HsHouseNewBuildingDao.insertSelective",record);
    }

    @Override
    public HsHouseNewBuilding selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHouseNewBuildingDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHouseNewBuilding record) {
        return this.getSqlSession().update("HsHouseNewBuildingDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHouseNewBuilding record) {
        return updateByPrimaryKeySelective(record);
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
        String sql  = "HsHouseNewBuildingDao.selectHsHouseNewBuildingListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsHouseNewBuildingDao.selectHsHouseNewBuildingListByCondition";
        }
        return executeSql(condition, sql);
    }

    @Override
    public List<Map<Object,Object>> getPropertyArea() {
        return this.getSqlSession().selectList("HsHouseNewBuildingDao.getPropertyArea");
    }

    @Override
    public List<Map<Object,Object>> getDevelopers(){
        return this.getSqlSession().selectList("HsHouseNewBuildingDao.getDevelopers");
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsHouseNewBuildingDao.selectCustomColumnNamesList");
    }
}
