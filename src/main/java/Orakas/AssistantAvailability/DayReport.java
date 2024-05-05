package Orakas.AssistantAvailability;
/*
Contains information about work done in specific day. Is used for reporting;
 */
public class DayReport {
    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }
    /*
    Which day of month
     */
    int day;
    /*
    Total worked hours
     */
    double worked = 0D;
    /*
    How many hours were worked at day
     */
    double workedAtDay = 0D;
    /*
    How many hours were worked at night
     */
    double workedAtNight = 0D;
    /*
   How many hours were worked at weekend
    */
    double workedAtWeekend = 0D;

    public double getWorkedAtWeekend() {
        return workedAtWeekend;
    }

    public void setWorkedAtWeekend(double workedAtWeekend) {
        this.workedAtWeekend = workedAtWeekend;
    }



    DayReport(double work, double workAtDay, double workedAtNight, double workedAtWeekend, int day){
    setWorked(work);
    setWorkedAtDay(workAtDay);
    setWorkedAtNight(workedAtNight);
    setWorkedAtWeekend(workedAtWeekend);
    setDay(day);
    }

    public double getWorked() {
        return worked;
    }

    public void setWorked(double worked) {
        this.worked = worked;
    }

    public double getWorkedAtDay() {
        return workedAtDay;
    }

    public void setWorkedAtDay(double workedAtDay) {
        this.workedAtDay = workedAtDay;
    }

    public double getWorkedAtNight() {
        return workedAtNight;
    }

    public void setWorkedAtNight(double workedAtNight) {
        this.workedAtNight = workedAtNight;
    }
}
