package com.example.demo.controller;

import com.example.demo.pojo.Users;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/14
 */
@RestController
public class UserController {

    @RequestMapping("/showUser")
    public String showUser(Model model){
        List<Users> usersList = new ArrayList<>();
        usersList.add(new Users(1,"wenbn1",16));
        usersList.add(new Users(2,"wenbn2",17));
        usersList.add(new Users(3,"wenbn3",18));
        usersList.add(new Users(4,"wenbn4",19));
        model.addAttribute("list",usersList);
        return "userList";
    }
}
