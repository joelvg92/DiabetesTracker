package com.diabetes.tracker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultSetNutritionOrder {
    private String id;
    private String foodName;
    private String mealSize;
    private String calories;
    private String time;
}
