/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadpso;

import java.util.List;

/**
 *
 * @author sanja
 */
public class PredictionResult {
    private String date;
    private String day;
    private String holidayName;
    private String type;
    private double predictedIncrease;

    public PredictionResult(String date, String day, String holidayName, String type, double predictedIncrease) {
        this.date = date;
        this.day = day;
        this.holidayName = holidayName;
        this.type = type;
        this.predictedIncrease = predictedIncrease;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public String getType() {
        return type;
    }

    public double getPredictedIncrease() {
        return predictedIncrease;
    }
}
