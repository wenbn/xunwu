package com.example.demo.service.impl;

import com.example.demo.dao.HsAdvertDataDao;
import com.example.demo.pojo.HsAdvertData;
import com.example.demo.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
@Service("commonService")
@Transactional
public class CommonServiceImpl<T> implements CommonService<T> {


    @Autowired
    @Qualifier("hsAdvertDataDaoImpl")
    private HsAdvertDataDao hsAdvertDataDao;

    @Override
    public void addAdvertData(HsAdvertData hsAdvertData) {
        hsAdvertDataDao.insert(hsAdvertData);
    }

    /**
     * 自定义查询列数据
     * @param condition 查询条件 List<String> columns
     * @return
     * @throws Exception
     */
    @Override
    public List<T> selectCustomColumnNamesList(T t, Map<Object, Object> condition) throws Exception {
        Map<Object,Object> result = new HashMap<Object,Object>();
        try{
            if (t.hashCode() ==  HsAdvertData.class.hashCode()) {//
                result = hsAdvertDataDao.selectCustomColumnNamesList(condition);
            }
            return (List<T>) result.get("data");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
