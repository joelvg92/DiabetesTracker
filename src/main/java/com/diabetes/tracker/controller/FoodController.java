package com.diabetes.tracker.controller;

import com.diabetes.tracker.Food.Food;
import com.diabetes.tracker.Food.Foodv2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@RestController
public class FoodController {
    @Autowired
    Food food;

    @GetMapping("/food/find")
    public Foodv2 getMedicationName(@RequestParam String foodName) throws InterruptedException, ExecutionException {
        String uri = "https://trackapi.nutritionix.com/v2/search/instant?query="+foodName;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-app-id","57812624");
        headers.add("x-app-key","ae86094b6c0369265174939ca0cb74dc");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, entity, Foodv2.class).getBody();
        //return ResponseEntity.ok().body(food.getFoodInfo(foodName));
    }
}
