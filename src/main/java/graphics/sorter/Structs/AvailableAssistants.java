package graphics.sorter.Structs;

import graphics.sorter.Assistant;
import graphics.sorter.Database;
import graphics.sorter.JsonManip;
import graphics.sorter.Settings;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;

public class AvailableAssistants implements Saveable {
    private int year;
    private int month;
    public ArrayList<ArrayList<Assistant>> getAvailableAssistantsAtDays() {
        return availableAssistantsAtDays;
    }

    public void setAvailableAssistantsAtDays(ArrayList<ArrayList<Assistant>> availableAssistantsAtDays) {
        this.availableAssistantsAtDays = availableAssistantsAtDays;
    }

    public ArrayList<ArrayList<Assistant>> getAvailableAssistantsAtNights() {
        return availableAssistantsAtNights;
    }

    public void setAvailableAssistantsAtNights(ArrayList<ArrayList<Assistant>> availableAssistantsAtNights) {
        this.availableAssistantsAtNights = availableAssistantsAtNights;
    }

    private ArrayList<ArrayList<Assistant>> availableAssistantsAtDays = new ArrayList();
    private ArrayList<ArrayList<Assistant>> availableAssistantsAtNights = new ArrayList();
    public void createNew(JsonManip map) throws IOException {
        Settings settings = Database.loadSettings();
        ListOfAssistants listOfA = Database.loadAssistants();
        AvailableAssistants availableAssistants = new AvailableAssistants();
        ArrayList<ArrayList<Assistant>> dayList = new ArrayList<>();
        ArrayList<ArrayList<Assistant>> nightList = new ArrayList<>();
        for(int i = 0; i < Month.of(settings.getCurrentMonth()).length(Year.isLeap(settings.getCurrentYear())); i++){
            ArrayList<Assistant> inputDayList = new ArrayList<>();
            ArrayList<Assistant> inputNightList = new ArrayList<>();
            dayList.add(inputDayList);
            nightList.add(inputNightList);
            for(Assistant asis : listOfA.getAssistantList()){
                if(asis.getWorkDays()[(LocalDate.of(settings.getCurrentYear(), settings.getCurrentMonth(), i+1).getDayOfWeek().getValue()-1)] == 1 ){
                    inputDayList.add(asis);
                }
                if(asis.getWorkDays()[(LocalDate.of(settings.getCurrentYear(), settings.getCurrentMonth(), i+1).getDayOfWeek().getValue()+6)] == 1 ){
                    inputNightList.add(asis);
                }

            }


        }

        availableAssistants.setAvailableAssistantsAtDays(dayList);
        availableAssistants.setAvailableAssistantsAtNights(nightList);
        Database.saveAssistantAvailability(settings.getCurrentYear(), settings.getCurrentMonth(),availableAssistants);
    }
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
