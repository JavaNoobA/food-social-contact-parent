package com.imooc.diners.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pengfei.zhao
 * @date 2020/12/23 20:04
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/diner")
    public String hello(@RequestParam("name") String name) {
        return "hello " + name;
    }
}
