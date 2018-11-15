package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/14
 */
@RestController
public class UploadController {

    @RequestMapping("/fileUploadController")
    public Map<Object,Object> upload(MultipartFile filename) throws IOException {
        Map<Object,Object> result = new HashMap<>();
        System.out.println(filename.getOriginalFilename());
        filename.transferTo(new File("e:/"+filename.getOriginalFilename()));
        result.put("state","success");
        return result;
    }
}
