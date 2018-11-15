package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsMemberHousingBargainDao;
import www.ucforward.com.entity.HsMemberHousingBargain;

import java.util.List;
import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/8/20
 * @Description:
 */
@Repository("hsMemberHousingBargainDao")
public class HsMemberHousingBargainDaoImpl extends BaseDao implements HsMemberHousingBargainDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsMemberHousingBargainDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsMemberHousingBargain record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsMemberHousingBargain record) {
        return this.getSqlSession().insert("HsMemberHousingBargainDao.insertSelective",record);
    }

    @Override
    public HsMemberHousingBargain selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsMemberHousingBargainDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsMemberHousingBargain record) {
        return this.getSqlSession().update("HsMemberHousingBargainDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsMemberHousingBargain record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        String sql = "HsMemberHousingBargainDao.selectMemberHousingApplyListMapByCondition";
        if(returnType != 0){
            sql = "HsMemberHousingBargainDao.selectMemberHousingApplyListByCondition";
        }
        return executeSql(condition,sql);
    }

    @Override
    public Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition){
        return executeSql(condition, "HsMemberHousingBargainDao.selectCustomColumnNamesList");
    }

    @Override
    public List<Map<Object,Object>> getMyBargainList(Map<Object, Object> condition){
        return this.getSqlSession().selectList("HsMemberHousingBargainDao.getMyBargainList" ,condition);
    }

    @Override
    public Integer updateCustomColumnByCondition(Map<Object,Object> condition){
        return this.getSqlSession().update("HsMemberHousingBargainDao.updateCustomColumnByCondition",condition);
    }
}
