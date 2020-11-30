package com.diabetes.tracker.medication;

import au.com.bytecode.opencsv.CSVReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
@Service
public class Medication {

    public List<String> getMedicineInfo(String medication) {
        List<String> foods = new ArrayList<>();
        CSVReader reader = null;
        try {
            InputStream input = new ClassPathResource("medicines.csv").getInputStream();
            reader = new CSVReader(new InputStreamReader(input), '\n');
            while (reader.readNext() != null) {
                for (String token : reader.readNext()) {
                    String splitRow[] = token.split(",");
                    if (splitRow[2].toLowerCase().contains(medication.toLowerCase())) {
                        foods.add(token);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return foods;
    }


    public static void main(String[] args) {
        Medication m = new Medication();
        List<String> medications = m.getMedicineInfo("metopro");
        for(String medication:medications){
            System.out.println(medication);
        }


    }
}