package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSysPlatformSettingDao;
import www.ucforward.com.entity.HsSysPlatformSetting;

import java.util.Map;

@Repository("hsSysPlatformSettingDao")
public class HsSysPlatformSettingDaoImpl extends BaseDao implements HsSysPlatformSettingDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsSysPlatformSettingDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsSysPlatformSetting record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsSysPlatformSetting record) {
        return this.getSqlSession().delete("HsSysPlatformSettingDao.insertSelective" ,record);
    }

    @Override
    public HsSysPlatformSetting selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsSysPlatformSettingDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSysPlatformSetting record) {
        return this.getSqlSession().update("HsSysPlatformSettingDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsSysPlatformSetting record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsSysPlatformSettingDao.selectCustomColumnNamesList");
    }
}