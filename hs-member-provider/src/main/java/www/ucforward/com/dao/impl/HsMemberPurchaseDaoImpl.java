package www.ucforward.com.dao.impl;


import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsMemberPurchaseDao;
import www.ucforward.com.entity.HsMemberPurchase;

import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/8/21 15:36
 * @Description:
 */
@Repository("hsMemberPurchaseDao")
public class HsMemberPurchaseDaoImpl extends BaseDao implements HsMemberPurchaseDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsMemberPurchaseDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsMemberPurchase record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsMemberPurchase record) {
        return this.getSqlSession().delete("HsMemberPurchaseDao.insertSelective" ,record);
    }

    @Override
    public HsMemberPurchase selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsMemberPurchaseDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsMemberPurchase record) {
        return this.getSqlSession().update("HsMemberPurchaseDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsMemberPurchase record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        //默认查询ListMap
        String sql  = "HsMemberPurchaseDao.selectHsMemberExtendListMapByCondition";
        if(returnType != 0){
            //查询实体
            sql  = "HsMemberPurchaseDao.selectHsMemberExtendListByCondition";
        }
        return executeSql(condition, sql);
    }

    @Override
    public Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition){
        return executeSql(condition, "HsMemberPurchaseDao.selectCustomColumnNamesList");
    }

}
