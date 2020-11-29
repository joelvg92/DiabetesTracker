package com.diabetes.tracker.controller;

import com.diabetes.tracker.service.NutritionOrderService;
import org.hl7.fhir.r4.model.NutritionOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class NutritionOrderController {
    @Autowired
    NutritionOrderService nutritionOrderService;

    @PostMapping("/nutritionOrder")
    public ResponseEntity<String> addNutritionOrder(@RequestBody Map<String, String> json ) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(nutritionOrderService.addNutritionOrder(json.get("patientId"),json.get("name"),json.get("dosage"),json.get("unit"),json.get("time")));
    }

    @PutMapping("/nutritionOrder/{id}")
    public ResponseEntity<String> updateNutritionOrder(@RequestBody Map<String, String> json) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(nutritionOrderService.updateNutritionOrder(json.get("id"),json.get("patientId"),json.get("name"),json.get("dosage"),json.get("unit"),json.get("time")));
    }

    @GetMapping("/nutritionOrder/{id}")
    public ResponseEntity<List<NutritionOrder>> getNutritionOrder(@PathVariable(value="id") String id) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(nutritionOrderService.getNutritionOrderByUser(id));
    }

    @DeleteMapping ("/nutritionOrder/{id}")
    public ResponseEntity<String> deleteNutritionOrder(@PathVariable(value="id") String id) throws InterruptedException, ExecutionException {
        return ResponseEntity.noContent().build();
    }
}
