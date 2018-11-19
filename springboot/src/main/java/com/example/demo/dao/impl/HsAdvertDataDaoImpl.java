package com.example.demo.dao.impl;

import com.example.demo.dao.BaseDao;
import com.example.demo.dao.HsAdvertDataDao;
import com.example.demo.pojo.HsAdvertData;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Map;


@Repository("hsAdvertDataDaoImpl")
public class HsAdvertDataDaoImpl extends BaseDao implements HsAdvertDataDao {


    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("com.example.demo.dao.HsAdvertDataDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsAdvertData record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsAdvertData record) {
        return this.getSqlSession().insert("com.example.demo.dao.HsAdvertDataDao.insertSelective" ,record);
    }

    @Override
    public HsAdvertData selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("com.example.demo.dao.HsAdvertDataDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsAdvertData record) {
        return this.getSqlSession().update("com.example.demo.dao.HsAdvertDataDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsAdvertData record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "com.example.demo.dao.HsAdvertDataDao.selectCustomColumnNamesList");
    }


}