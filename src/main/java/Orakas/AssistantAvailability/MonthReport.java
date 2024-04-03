package Orakas.AssistantAvailability;

import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.UUID;

public class MonthReport {

    String name;
    UUID ID;



    int month;
    int year;
    double workFund= 0D;
    double workedTotal = 0D;
    double workedAtDayTotal = 0D;
    double workedAtNightTotal = 0D;
    ArrayList<DayReport> dailyReports = new ArrayList<>();
    MonthReport(UUID assistantID,double workFundI, double workedTotalI, double workedAtDayTotal, double workedAtNightTotal, int year, int month ){
        setID(assistantID);
        setWorkFund(workFundI);
        setWorkedTotal(workedTotalI);
        setWorkedAtDayTotal(workedAtDayTotal);
        setWorkedAtNightTotal(workedAtNightTotal);
        setYear(year);
        setMonth(month);
        setDailyReports(new ArrayList<>(Month.of(month).length(Year.isLeap(year))));

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getID() {
        return ID;
    }

    public void setID(UUID ID) {
        this.ID = ID;
    }

    public double getWorkFund() {
        return workFund;
    }

    public void setWorkFund(double workFund) {
        this.workFund = workFund;
    }

    public double getWorkedTotal() {
        return workedTotal;
    }

    public void setWorkedTotal(double workedTotal) {
        this.workedTotal = workedTotal;
    }

    public double getWorkedAtDayTotal() {
        return workedAtDayTotal;
    }

    public void setWorkedAtDayTotal(double workedAtDayTotal) {
        this.workedAtDayTotal = workedAtDayTotal;
    }

    public double getWorkedAtNightTotal() {
        return workedAtNightTotal;
    }

    public void setWorkedAtNightTotal(double workedAtNightTotal) {
        this.workedAtNightTotal = workedAtNightTotal;
    }

    public ArrayList<DayReport> getDailyReports() {
        return dailyReports;
    }

    public void setDailyReports(ArrayList<DayReport> dailyReports) {
        this.dailyReports = dailyReports;
    }
    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
