package com.diabetes.tracker.controller;

import com.diabetes.tracker.service.FHIRService;
import com.diabetes.tracker.model.User;
import com.diabetes.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    FHIRService fhirService;

    @GetMapping("/user")
    public ResponseEntity<User> getUser(@RequestParam String name ) throws InterruptedException, ExecutionException{
        return ResponseEntity.ok().body(userService.getUserDetails(name));
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody User user ) throws InterruptedException, ExecutionException {
            String id = fhirService.createPatientFirebase(user.getName(), user.getGender(), user.getDob());
            user.setPatientId(id);
            userService.saveUserDetails(user);
           return ResponseEntity.ok().body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> validateUser(@RequestBody User user) throws InterruptedException, ExecutionException{
        User u = userService.validateUser(user.getUsername(),user.getPassword());
        if(u!=null){
            return ResponseEntity.ok().body(u);
        }else{
            return ResponseEntity.status(401).body(user);
        }
    }

    @PutMapping("/user")
    public String updateUser(@RequestBody User User  ) throws InterruptedException, ExecutionException {
        return userService.updateUserDetails(User);
    }

    @DeleteMapping("/user")
    public String deleteUser(@RequestParam String name){
        return userService.deleteUser(name);
    }
}