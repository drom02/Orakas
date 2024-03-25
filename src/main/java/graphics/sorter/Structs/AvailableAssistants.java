package graphics.sorter.Structs;

import graphics.sorter.Assistant;
import graphics.sorter.AssistantAvailability.AssistantAvailability;
import graphics.sorter.AssistantAvailability.AvailableAssistantsLocalDateTime;
import graphics.sorter.AssistantAvailability.DateTimeAssistantAvailability;
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
    public ArrayList<ArrayList<AssistantAvailability>> getAvailableAssistantsAtDays() {
        return availableAssistantsAtDays;
    }

    public void setAvailableAssistantsAtDays(ArrayList<ArrayList<AssistantAvailability>> availableAssistantsAtDays) {
        this.availableAssistantsAtDays = availableAssistantsAtDays;
    }

    public ArrayList<ArrayList<AssistantAvailability>> getAvailableAssistantsAtNights() {
        return availableAssistantsAtNights;
    }

    public void setAvailableAssistantsAtNights(ArrayList<ArrayList<AssistantAvailability>> availableAssistantsAtNights) {
        this.availableAssistantsAtNights = availableAssistantsAtNights;
    }
    public AvailableAssistantsLocalDateTime convertToDateTimeAvailability(int year, int month){
        ArrayList<ArrayList<DateTimeAssistantAvailability>> outputDay = new ArrayList<>(31);
        ArrayList<ArrayList<DateTimeAssistantAvailability>> outputNight = new ArrayList<>(31);
        int iter = 1;
        for(ArrayList<AssistantAvailability> arrayAssistant: getAvailableAssistantsAtDays()){
            ArrayList<DateTimeAssistantAvailability> tempArray = new ArrayList<>(15);
            for(AssistantAvailability ar : arrayAssistant){
                DateTimeAssistantAvailability temp = new DateTimeAssistantAvailability(year,month,iter,true,ar);
                tempArray.add(temp);
            }
            iter++;
            outputDay.add(tempArray);
        }
         iter = 1;
        for(ArrayList<AssistantAvailability> arrayAssistant: getAvailableAssistantsAtNights()){
            ArrayList<DateTimeAssistantAvailability> tempArray = new ArrayList<>(15);
            for(AssistantAvailability ar : arrayAssistant){
                DateTimeAssistantAvailability temp = new DateTimeAssistantAvailability(year,month,iter,false,ar);
                tempArray.add(temp);
            }
            iter++;
            outputNight.add(tempArray);
        }
        return new AvailableAssistantsLocalDateTime(outputDay,outputNight);
    }
    private ArrayList<ArrayList<AssistantAvailability>> availableAssistantsAtDays = new ArrayList();
    private ArrayList<ArrayList<AssistantAvailability>> availableAssistantsAtNights = new ArrayList();
    public void createNew(JsonManip map) throws IOException {
        Settings settings =Settings.getSettings();
        ListOfAssistants listOfA = Database.loadAssistants();
        AvailableAssistants availableAssistants = new AvailableAssistants();
        ArrayList<ArrayList<AssistantAvailability>> dayList = new ArrayList<>();
        ArrayList<ArrayList<AssistantAvailability>> nightList = new ArrayList<>();
        for(int i = 0; i < Month.of(settings.getCurrentMonth()).length(Year.isLeap(settings.getCurrentYear())); i++){
            ArrayList<AssistantAvailability> inputDayList = new ArrayList<>();
            ArrayList<AssistantAvailability> inputNightList = new ArrayList<>();
            dayList.add(inputDayList);
            nightList.add(inputNightList);
            for(Assistant asis : listOfA.getAssistantList()){
                if(asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue()).getState()){
                    inputDayList.add(new AssistantAvailability(asis.getID(),asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue()).getAvailability()));
                }
                if(!asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue() + 6).getState()){
                    inputNightList.add(new AssistantAvailability(asis.getID(),asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue()+ 6).getAvailability()));
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
