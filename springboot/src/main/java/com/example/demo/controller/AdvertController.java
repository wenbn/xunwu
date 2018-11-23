package com.example.demo.controller;

import com.example.demo.pojo.HsAdvertData;
import com.example.demo.serviceInter.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.expression.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @RequestMapping("list")
    public List<Map<Object,Object>> advertList() throws Exception {

        int i = Integer.MIN_VALUE;
        if(i==-i){
            System.out.println("true....."+i);
        }
        Map<Object,Object> condition = new HashMap<>();
        //自定义查询的列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("LANGUAGE_VERSION languageVersion");//城市id
        queryColumn.add("AD_FULL_TITLE adFullTitle");//积分编码
        queryColumn.add("AD_SHORT_TITLE AD_SHORT_TITLE");//控制加分还是减分 0 ：加分 1减分
        queryColumn.add("AD_TYPE AD_TYPE");//积分值
        queryColumn.add("AD_IMG_URL AD_IMG_URL");//积分值
        queryColumn.add("AD_CONTENT AD_CONTENT");//积分值
        queryColumn.add("AD_SN AD_SN");//积分值
        queryColumn.add("AD_C_COUNT AD_C_COUNT");//积分值
        queryColumn.add("AD_F_COUNT AD_F_COUNT");//积分值
        queryColumn.add("AD_DESC AD_DESC");//积分值
        queryColumn.add("AD_LINK AD_LINK");//积分值
        condition.put("queryColumn", queryColumn);
        condition.put("pageIndex", 0);
        condition.put("pageSize", 10);
        List list = commonService.selectCustomColumnNamesList(HsAdvertData.class, condition);
        return list;
    }
}
