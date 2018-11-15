package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHouseNewBuildingMemberApplyDao;
import www.ucforward.com.entity.HsHouseNewBuildingMemberApply;

import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/8/30 19:53
 * @Description:
 */
@Repository("houseNewBuildingMemberApplyDao")
public class HsHouseNewBuildingMemberApplyDaoImpl extends BaseDao implements HsHouseNewBuildingMemberApplyDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHouseNewBuildingMemberApplyDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHouseNewBuildingMemberApply record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHouseNewBuildingMemberApply record) {
        return this.getSqlSession().insert("HsHouseNewBuildingMemberApplyDao.insertSelective",record);
    }

    @Override
    public HsHouseNewBuildingMemberApply selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHouseNewBuildingMemberApplyDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHouseNewBuildingMemberApply record) {
        return this.getSqlSession().update("HsHouseNewBuildingMemberApplyDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHouseNewBuildingMemberApply record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsHouseNewBuildingMemberApplyDao.selectCustomColumnNamesList");
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
        String sql  = "HsHouseNewBuildingMemberApplyDao.selectHsHouseNewBuildingMemberListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsHouseNewBuildingMemberApplyDao.selectHsHouseNewBuildingMemberListByCondition";
        }
        return executeSql(condition, sql);
    }
}
