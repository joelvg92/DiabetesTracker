package com.diabetes.tracker.usermanagement.controller;

import com.diabetes.tracker.usermanagement.model.User;
import com.diabetes.tracker.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/getUserDetails")
    public User getUser(@RequestParam String name ) throws InterruptedException, ExecutionException{
        return userService.getUserDetails(name);
    }

    @PostMapping("/createUser")
    public String createUser(@RequestBody User User ) throws InterruptedException, ExecutionException {
        return userService.saveUserDetails(User);
    }

    @PutMapping("/updateUser")
    public String updateUser(@RequestBody User User  ) throws InterruptedException, ExecutionException {
        return userService.updateUserDetails(User);
    }

    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String name){
        return userService.deleteUser(name);
    }
}