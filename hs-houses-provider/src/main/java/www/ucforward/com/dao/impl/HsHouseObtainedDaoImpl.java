package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHouseObtainedDao;
import www.ucforward.com.entity.HsHouseObtained;

import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/9/18 17:50
 * @Description:
 */
@Repository("hsHouseObtainedDao")
public class HsHouseObtainedDaoImpl extends BaseDao implements HsHouseObtainedDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHouseObtainedDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHouseObtained record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHouseObtained record) {
        return this.getSqlSession().insert("HsHouseObtainedDao.insertSelective",record);
    }

    @Override
    public HsHouseObtained selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHouseObtainedDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHouseObtained record) {
        return this.getSqlSession().update("HsHouseObtainedDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHouseObtained record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsHouseObtainedDao.selectCustomColumnNamesList");
    }
}
