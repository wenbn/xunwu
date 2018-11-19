package com.example.demo.serviceInter;

import com.example.demo.pojo.HsAdvertData;
import org.springframework.stereotype.Service;


/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
@Service("commonService")
public interface CommonService<T>{

    //添加广告
    public void addAdvertData(HsAdvertData hsAdvertData);
}
