package com.example.demo.service;

import com.example.demo.pojo.HsAdvertData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
@Service("commonService")
public interface CommonService<T>{

    //添加广告
    public void addAdvertData(HsAdvertData hsAdvertData);


    /**
     * 自定义查询列数据
     * @param condition 查询条件 List<String> columns
     * @return
     * @throws Exception
     */
    public List<T> selectCustomColumnNamesList(T t, Map<Object, Object> condition) throws Exception;
}
