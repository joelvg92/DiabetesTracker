package com.diabetes.tracker.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultSetMedicaiton {
    private String id;
    private String medicationName;
    private String dosage;
    private String unit;
    private String time;

}
