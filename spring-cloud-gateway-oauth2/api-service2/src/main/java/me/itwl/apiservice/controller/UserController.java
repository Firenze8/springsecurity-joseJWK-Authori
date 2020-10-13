package me.itwl.apiservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//https://www.jianshu.com/p/1e974fc91f74
@RestController
@RequestMapping("/user")
public class UserController {

//    @Autowired
//    private LoginUserHolder loginUserHolder;

//    @GetMapping("/currentUser")
//    public UserDTO currentUser() {
//        return loginUserHolder.getCurrentUser();
//    }

    @GetMapping("/hello")
    public String getRequest() {
        return "Hello World user.";
    }
}
