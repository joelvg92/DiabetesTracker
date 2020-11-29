package com.diabetes.tracker.controller;

import com.diabetes.tracker.service.ObservationService;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class ObservationController {
    @Autowired
    ObservationService observationService;

    @PostMapping ("/observation")
    public ResponseEntity<String> addObservation(@RequestBody Map<String, String> json ) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(observationService.addObservation(json.get("patientId"),json.get("reading"),json.get("description"),json.get("time")));
    }

    @PutMapping ("/observation/{id}")
    public ResponseEntity<String> updateObservation(@RequestBody Map<String, String> json) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(observationService.updateObservation(json.get("id"),json.get("patientId"),json.get("reading"),json.get("description"),json.get("time")));
    }

    @GetMapping ("/observation/{id}")
    public ResponseEntity<List<Observation>> getObservation(@PathVariable(value="id") String id) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(observationService.getObservationByUser(id));
    }

    @DeleteMapping ("/observation/{id}")
    public ResponseEntity<String> deleteObservation(@PathVariable(value="id") String id) throws InterruptedException, ExecutionException {
        return ResponseEntity.noContent().build();
    }
}
