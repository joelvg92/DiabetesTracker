package com.diabetes.tracker.usermanagement.controller;

import com.diabetes.tracker.usermanagement.model.User;
import com.diabetes.tracker.usermanagement.service.FHIRService;
import com.diabetes.tracker.usermanagement.service.UserService;
import org.hl7.fhir.r4.model.Patient;
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

    @GetMapping("/getUserDetails")
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

    @PutMapping("/updateUser")
    public String updateUser(@RequestBody User User  ) throws InterruptedException, ExecutionException {
        return userService.updateUserDetails(User);
    }

    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String name){
        return userService.deleteUser(name);
    }
}