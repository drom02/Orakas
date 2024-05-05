package Orakas;

import Orakas.Database.Database;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
/*
Class responsible for management of general settings of the app. In the future should be replaced by simple Json.
 */
public class Settings {
    private static Settings settings;
    public double getStandardWorkDay() {
        return standardWorkDay;
    }

    public void setStandardWorkDay(double standardWorkDay) {
        this.standardWorkDay = standardWorkDay;
    }
    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public int[] getDefStart() {
        return deftStart;
    }

    public void setDeftStart(int[] deftStart) {
        this.deftStart = deftStart;
    }

    public int[] getDefEnd() {
        return defEnd;
    }

    public void setDefEnd(int[] defEnd) {
        this.defEnd = defEnd;
    }
    public int getMaxShiftLength() {
        return maxShiftLength;
    }

    public void setMaxShiftLength(int maxShiftLength) {
        this.maxShiftLength = maxShiftLength;
    }
    private int currentYear;
    private int currentMonth;
    private int[] deftStart;
    private int[] defEnd;
    private int maxShiftLength;
    private double standardWorkDay;

    public String getFilePath() {
        return filePath;
    }
    public static Settings getSettings(){
        if (settings == null) {
            // if instance is null, initialize
            settings = Database.loadSettings();
            System.out.println("Settings loaded");
        }
        return settings;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private String filePath;

    public static Settings createNewSettingsFile() throws IOException {
        String st =  System.getenv("APPDATA")+"\\Local\\ORAKAS\\";
        Settings defset = new Settings(12,2024, st,new int[]{8,30},new int[]{20,30},12,7.5);
       // Database.saveSettings(defset);
        return defset;

    }

    @JsonCreator
    private Settings(@JsonProperty("currentMonth")int currentMonth, @JsonProperty("currentYear")int currentYear,
                     @JsonProperty("filePath")String filePath,@JsonProperty("defStart")int[] defstart,
                     @JsonProperty("defend")int[] defend,@JsonProperty("maxShiftLength")int maxShiftLength,
                     @JsonProperty("standardWorkDay")double standardWorkDay){
            setCurrentMonth(currentMonth);
            setCurrentYear(currentYear);
            setFilePath(filePath);
            setDeftStart(defstart);
            setDefEnd(defend);
            setMaxShiftLength(maxShiftLength);
            setStandardWorkDay(standardWorkDay);
    }
}
