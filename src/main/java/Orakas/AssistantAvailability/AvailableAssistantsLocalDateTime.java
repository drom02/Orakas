package Orakas.AssistantAvailability;

import java.util.ArrayList;

public class AvailableAssistantsLocalDateTime {

    ArrayList<ArrayList<DateTimeAssistantAvailability>> localDateTimeDay = new ArrayList<>();
    ArrayList<ArrayList<DateTimeAssistantAvailability>> localDateTimeNight = new ArrayList<>();
    public  AvailableAssistantsLocalDateTime(ArrayList<ArrayList<DateTimeAssistantAvailability>> localDateTimeDay,ArrayList<ArrayList<DateTimeAssistantAvailability>> localDateTimeNight){
        setLocalDateTimeDay(localDateTimeDay);
        setLocalDateTimeNight(localDateTimeNight);
    }


    public ArrayList<ArrayList<DateTimeAssistantAvailability>> getLocalDateTimeDay() {
        return localDateTimeDay;
    }

    public void setLocalDateTimeDay(ArrayList<ArrayList<DateTimeAssistantAvailability>> localDateTimeDay) {
        this.localDateTimeDay = localDateTimeDay;
    }

    public ArrayList<ArrayList<DateTimeAssistantAvailability>> getLocalDateTimeNight() {
        return localDateTimeNight;
    }

    public void setLocalDateTimeNight(ArrayList<ArrayList<DateTimeAssistantAvailability>> localDateTimeNight) {
        this.localDateTimeNight = localDateTimeNight;
    }
}
