package com.example.demo.dao.impl;

import com.example.demo.dao.HsAdvertDataDao;
import com.example.demo.pojo.HsAdvertData;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;


@Repository("hsAdvertDataDaoImpl")
public class HsAdvertDataDaoImpl extends SqlSessionDaoSupport implements HsAdvertDataDao {


    /**
     * 需要继承
     * @param sqlSessionFactory
     */
    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }


    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsAdvertDataDao.deleteByPrimaryKey" ,id);
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
        return this.getSqlSession().selectOne("HsAdvertDataDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsAdvertData record) {
        return this.getSqlSession().update("HsAdvertDataDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsAdvertData record) {
        return updateByPrimaryKeySelective(record);
    }


}