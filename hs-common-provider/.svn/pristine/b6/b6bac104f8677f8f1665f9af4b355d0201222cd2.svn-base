package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsMsgSettingDao;
import www.ucforward.com.entity.HsMsgSetting;

import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/8/26 16:51
 * @Description:
 */
@Repository("hsMsgSettingDao")
public class HsMsgSettingDaoImpl extends BaseDao implements HsMsgSettingDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsMsgSettingDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsMsgSetting record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsMsgSetting record) {
        return this.getSqlSession().insert("HsMsgSettingDao.insertSelective" ,record);
    }

    @Override
    public HsMsgSetting selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsMsgSettingDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsMsgSetting record) {
        return this.getSqlSession().update("HsMsgSettingDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsMsgSetting record) {
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
        String sql  = "HsMsgSettingDao.selectHsMsgSettingListMapByCondition";
        if(returnType!=0){
            //查询实体
            sql  = "HsMsgSettingDao.selectHsMsgSettingListByCondition";
        }
        return executeSql(condition, sql);
    }
}
