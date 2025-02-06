/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loadpso;


import java.text.SimpleDateFormat;
import java.util.Date;

public class HolidayFeature {
    private String date;
    private String day;
    private String holidayName;
    private String type;

    public HolidayFeature(String date, String day, String holidayName, String type) {
        this.date = date;
        this.day = day;
        this.holidayName = holidayName;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

   public String getHolidayName() {
    if (holidayName.equals("None") && isWeekend()) {
        return "Weekend Holiday";
    }
    return holidayName;
}

public String getType() {
    if (type.equals("Regular") && isWeekend()) {
        return "Weekend";
    }
    return type;
}


    public double[] toFeatureArray() {
        double[] features = new double[7];
        features[0] = isWeekend() ? 1 : 0;
        features[1] = isFestival() ? 1 : 0;
        features[2] = isPublicHoliday() ? 1 : 0;
        features[3] = getSeasonalEffect();
        features[4] = getDayOfWeekNumeric();  
        features[5] = getMonthFeature();      
        features[6] = isLongWeekend() ? 1 : 0;  
        return features;
    }

    

    private boolean isFestival() {
        return type.equalsIgnoreCase("Festival");
    }

    private boolean isPublicHoliday() {
        return type.equalsIgnoreCase("Public");
    }

    private double getSeasonalEffect() {
        int month = getMonthFromDate();
        switch (month) {
            case 2:
                return 1.2; 
            case 12: 
                return 1.3; 
            case 6: case 7: 
                return 0.9; 
            default:
                return 1.0;
        }
    }

    int getMonthFromDate() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date parsedDate = format.parse(date);
            return Integer.parseInt(new SimpleDateFormat("MM").format(parsedDate));
        } catch (Exception e) {
            return 0;
        }
    }

    private double getDayOfWeekNumeric() {
        switch(day.toLowerCase()) {
            case "monday": return 1;
            case "tuesday": return 2;
            case "wednesday": return 3;
            case "thursday": return 4;
            case "friday": return 5;
            case "saturday": return 6;
            case "sunday": return 7;
            default: return 0;
        }
    }

    private double getMonthFeature() {
        int month = getMonthFromDate();
        if (month == 12 || month == 1) return 1.5;  
        if (month == 10 || month == 11) return 1.4; 
        if (month == 8 || month == 9) return 1.3;   
        return 1.0;
    }

    private boolean isLongWeekend() {
        return isWeekend() && (type.equalsIgnoreCase("Holiday") || type.equalsIgnoreCase("Festival"));
    }

  public double estimatedLoad() {
    double baseLoad = 100.0;
    
   
    if (type.equalsIgnoreCase("Festival")) {
        return baseLoad * 1.26;
    }
    
    else if (type.equalsIgnoreCase("Holiday")) {
        return baseLoad * 0.76; 
    }
    
    else if (isWeekend()) {
        return baseLoad * 0.25;
    }
   
    return 0.0;
}

public boolean isWeekend() {
    return day.equalsIgnoreCase("Saturday") || day.equalsIgnoreCase("Sunday");
}
}