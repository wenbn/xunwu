package com.example.demo.controller;

import com.sun.javafx.collections.MappingChange;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/13
 */
@RestController
public class SampleController {

    @RequestMapping("/")
    public Map<Object,Object> home() {
        Map<Object,Object> data = new HashMap<>();
        data.put("name","wenbn");
        data.put("date",new Date());
        data.put("age",17);
        return data;
    }
}