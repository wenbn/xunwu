package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/14
 */
@Controller
public class ThymeleafController {

    @RequestMapping("getInfo")
    public String getInfo(Model model){
        model.addAttribute("msg","Thymeleaf 第一个案例");
        return "thymeleaf";
    }

}
