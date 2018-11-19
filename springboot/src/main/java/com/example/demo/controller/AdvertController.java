package com.example.demo.controller;

import com.example.demo.pojo.HsAdvertData;
import com.example.demo.serviceInter.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/19
 */
@RestController
public class AdvertController {

    @Autowired
    private CommonService commonService;

    @RequestMapping("add")
    public String addAdvert(HsAdvertData advertData){
        advertData.setAdDesc("efefefe");
        advertData.setAdCCount(1212);
        commonService.addAdvertData(advertData);
        return "success";
    }
}
