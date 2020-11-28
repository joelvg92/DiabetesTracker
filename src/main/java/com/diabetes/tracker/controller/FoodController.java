package com.diabetes.tracker.controller;

import com.diabetes.tracker.Food.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class FoodController {
    @Autowired
    Food food;
    @GetMapping("/food/find")
    public ResponseEntity<List<String>> getMedicationName(@RequestParam String foodName) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(food.getFoodInfo(foodName));
    }
}
