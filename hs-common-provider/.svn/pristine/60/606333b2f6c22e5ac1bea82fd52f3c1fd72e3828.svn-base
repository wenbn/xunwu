package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsMsgRecordDao;
import www.ucforward.com.entity.HsMsgRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/8/26 16:51
 * @Description:
 */
@Repository("hsMsgRecordDao")
public class HsMsgRecordDaoImpl extends BaseDao implements HsMsgRecordDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsMsgRecordDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsMsgRecord record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsMsgRecord record) {
        return this.getSqlSession().insert("HsMsgRecordDao.insertSelective" ,record);
    }

    @Override
    public HsMsgRecord selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsMsgRecordDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsMsgRecord record) {
        return this.getSqlSession().update("HsMsgRecordDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsMsgRecord record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateCustomColumnByCondition(Map<Object,Object> condition){
        return this.getSqlSession().update("HsMsgRecordDao.updateCustomColumnByCondition",condition);
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
        String sql  = "HsMsgRecordDao.selectHsMsgRecordListMapByCondition";
        if(returnType!=0){
            //查询实体
            sql  = "HsMsgRecordDao.selectHsMsgRecordListByCondition";
        }
        return executeSql(condition, sql);
    }

    @Override
    public Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition){
        return executeSql(condition, "HsMsgRecordDao.selectCustomColumnNamesList");
    }

    @Override
    public List<Map<String, Object>> getMsgList(Map<Object,Object> condition) {
        return this.getSqlSession().selectList("HsMsgRecordDao.getMsgList",condition);
    }

    @Override
    public List<Map<String, Object>> getMsgType(String type) {
        return this.getSqlSession().selectList("HsMsgRecordDao.getMsgType",type);
    }

}
