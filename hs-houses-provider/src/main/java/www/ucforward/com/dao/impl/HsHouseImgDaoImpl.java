package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHouseImgDao;
import www.ucforward.com.dao.HsOwnerHousingApplicationDao;
import www.ucforward.com.entity.HsHouseImg;
import www.ucforward.com.entity.HsOwnerHousingApplication;

import java.util.Map;

@Repository("houseImgDao")
public class HsHouseImgDaoImpl extends BaseDao implements HsHouseImgDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHouseImgDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHouseImg record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHouseImg record) {
        return this.getSqlSession().insert("HsHouseImgDao.insertSelective",record);
    }

    @Override
    public HsHouseImg selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHouseImgDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHouseImg record) {
        return this.getSqlSession().update("HsHouseImgDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHouseImg record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        //默认查询ListMap
        String sql  = "HsHouseImgDao.selectHsHouseImgListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsHouseImgDao.selectHsHouseImgListByCondition";
        }
        return executeSql(condition, sql);
    }
}