package com.diabetes.tracker.controller;

import com.diabetes.tracker.medication.Medication;
import com.diabetes.tracker.service.MedicationService;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class MedicationController {
    @Autowired
    MedicationService medicationService;
    @Autowired
    Medication medication;

    @PostMapping ("/medication")
    public ResponseEntity<String> addMedication(@RequestBody Map<String, String> json ) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(medicationService.addMedication(json.get("patientId"),json.get("name"),json.get("dosage"),json.get("unit"),json.get("time")));
    }

    @PutMapping ("/medication/{id}")
    public ResponseEntity<String> updateMedication(@RequestBody Map<String, String> json) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(medicationService.updateMedication(json.get("id"),json.get("patientId"),json.get("name"),json.get("dosage"),json.get("unit"),json.get("time")));
    }

    @GetMapping ("/medication/{id}")
    public ResponseEntity<List<MedicationAdministration>> getMedication(@PathVariable(value="id") String id) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(medicationService.getMedicationByUser(id));
    }

    @GetMapping ("/medication/find")
    public ResponseEntity<List<String>> getMedicationName(@RequestParam String medicationName) throws InterruptedException, ExecutionException {
        return ResponseEntity.ok().body(medication.getMedicineInfo(medicationName));
    }

    @DeleteMapping ("/medication/{id}")
    public ResponseEntity<String> deleteMedication(@PathVariable(value="id") String id) throws InterruptedException, ExecutionException {
        return ResponseEntity.noContent().build();
    }
}
