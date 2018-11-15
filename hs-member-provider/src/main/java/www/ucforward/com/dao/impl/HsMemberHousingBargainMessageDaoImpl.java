package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsMemberHousingBargainMessageDao;
import www.ucforward.com.entity.HsMemberHousingBargainMessage;

import java.util.List;
import java.util.Map;

@Repository("hsMemberHousingBargainMessageDao")
public class HsMemberHousingBargainMessageDaoImpl extends BaseDao implements HsMemberHousingBargainMessageDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsMemberHousingBargainMessageDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsMemberHousingBargainMessage record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsMemberHousingBargainMessage record) {
        return this.getSqlSession().insert("HsMemberHousingBargainMessageDao.insertSelective",record);
    }

    @Override
    public HsMemberHousingBargainMessage selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsMemberHousingBargainMessageDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsMemberHousingBargainMessage record) {
        return this.getSqlSession().update("HsMemberHousingBargainMessageDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsMemberHousingBargainMessage record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsMemberHousingBargainMessageDao.selectCustomColumnNamesList");
    }

    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        String sql = "HsMemberHousingBargainMessageDao.selectHsMemberHousingBargainMessageListByCondition";
        return executeSql(condition,sql);
    }

    @Override
    public List<Map<String, Object>> getLastMsg(Map<Object, Object> condition) {
        return this.getSqlSession().selectList("HsMemberHousingBargainMessageDao.getLastMsg",condition);
    }

    @Override
    public Integer updateCustomColumnByCondition(Map<Object,Object> condition){
        return this.getSqlSession().update("HsMemberHousingBargainMessageDao.updateCustomColumnByCondition",condition);
    }
}