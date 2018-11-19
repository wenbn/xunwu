package com.example.demo.dao;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.utils.StringUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
public class BaseDao extends SqlSessionDaoSupport {
    /**
     * 需要继承
     * @param sqlSessionFactory
     */
    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    /**
     * 执行SQL
     * @param condition
     * @param sql
     * @return
     */
    protected Map<Object, Object> executeSql(Map<Object, Object> condition, String sql) {
        String _pageIndex = StringUtil.trim(condition.get("pageIndex"));
        String _pageSize = StringUtil.trim(condition.get("pageSize"));
        if(StringUtil.hasText(_pageIndex)&& StringUtil.hasText(_pageSize)){
            int pageIndex = StringUtil.getAsInt(_pageIndex);
            int pageSize = StringUtil.getAsInt(_pageSize);
            PageHelper.startPage(pageIndex,pageSize);
            List<Object> resultList = this.getSqlSession().selectList(sql ,condition);
            condition.clear();
            if(resultList==null || resultList.size()<1){
                condition.put("data",null);
                condition.put("pageInfo",null);
                return condition;
            }
            PageInfo result = new PageInfo(resultList);
            result.setList(null);
            condition.put("data",resultList);
            condition.put("pageInfo",result);
            return condition;
        }else{
            List<Object> resultList = this.getSqlSession().selectList(sql ,condition);
            condition.clear();
            condition.put("data",resultList);
            condition.put("pageInfo",null);
            return condition;
        }
    }
}
