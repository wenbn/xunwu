package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsMemberDirectPurchaseApplyDao;
import www.ucforward.com.entity.HsMemberDirectPurchaseApply;

import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/8/31 10:10
 * @Description:
 */
@Repository("hsMemberDirectPurchaseApplyDao")
public class HsMemberDirectPurchaseApplyDaoImpl extends BaseDao implements HsMemberDirectPurchaseApplyDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsMemberDirectPurchaseApplyDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsMemberDirectPurchaseApply record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsMemberDirectPurchaseApply record) {
        return this.getSqlSession().delete("HsMemberDirectPurchaseApplyDao.insertSelective" ,record);
    }

    @Override
    public HsMemberDirectPurchaseApply selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsMemberDirectPurchaseApplyDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsMemberDirectPurchaseApply record) {
        return this.getSqlSession().update("HsMemberDirectPurchaseApplyDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsMemberDirectPurchaseApply record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        //默认查询ListMap
        String sql  = "HsMemberDirectPurchaseApplyDao.selectHsMemberDirectPurchaseApplyListMapByCondition";
        if(returnType != 0){
            //查询实体
            sql  = "HsMemberDirectPurchaseApplyDao.selectHsMemberDirectPurchaseApplyListByCondition";
        }
        return executeSql(condition, sql);
    }
}
