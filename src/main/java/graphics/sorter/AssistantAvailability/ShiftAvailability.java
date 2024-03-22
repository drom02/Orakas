package graphics.sorter.AssistantAvailability;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Database;
import graphics.sorter.Settings;

import java.time.DayOfWeek;
import java.util.ArrayList;

public class ShiftAvailability {
    DayOfWeek day;

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public boolean getIsDay() {
        return isDay;
    }

    public void setIsDay(boolean day) {
        isDay = day;
    }

    boolean isDay;
    boolean state;
    Availability availability;
    @JsonCreator
    public ShiftAvailability(@JsonProperty("day")DayOfWeek day, @JsonProperty("isDay")boolean isDay,@JsonProperty("state") boolean state, @JsonProperty("availability")Availability availability) {
        setDay(day);
        setIsDay(isDay);
        setAvailability(availability);
        setState(state);
    }
    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }
    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
    public static ArrayList<ShiftAvailability> generateWeek(){
        Settings set = Settings.getSettings();
        ArrayList<ShiftAvailability> output = new ArrayList<>(14);
        if (set != null) {
           int[] start = set.getDeftStart();
           int[] end =  set.getDefEnd();
           for(int i =1; i<=7;i++){
               output.add(new ShiftAvailability(DayOfWeek.of(i),true, true,new Availability(start[0],start[1],end[0],end[1])));
           }
            for(int i =8; i<=14;i++){
                output.add(new ShiftAvailability(DayOfWeek.of(i-7),false, true,new Availability(end[0],end[1],start[0],start[1])));
            }
           return  output;
        }
        return null;
    }

}
