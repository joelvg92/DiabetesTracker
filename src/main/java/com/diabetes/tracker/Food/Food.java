package com.diabetes.tracker.Food;

import au.com.bytecode.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Service
public class Food {
    public List<String> getFoodInfo(String food) {
        List<String> foods = new ArrayList<>();
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(ResourceUtils.getFile("classpath:food.csv")), '\n');
            while (reader.readNext() != null) {
                for (String token : reader.readNext()) {
                    String splitRow[] = token.split(",");
                    if (splitRow[2].toLowerCase().contains(food.toLowerCase())) {
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

}
