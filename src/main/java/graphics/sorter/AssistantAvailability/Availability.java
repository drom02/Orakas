package graphics.sorter.AssistantAvailability;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;

public class Availability {
    @JsonCreator
    public Availability(@JsonProperty("startHours") int startHours,
                        @JsonProperty("startMinutes") int startMinutes,
                        @JsonProperty("endHours") int endHours,
                        @JsonProperty("endMinutes") int endMinutes ){
    setStartHours(startHours);
    setStartMinutes(startMinutes);
    setEndHours(endHours);
    setEndMinutes(endMinutes);

    }
    private int[] start = new int[2];
    private int[] end = new int[2];
@JsonIgnore
public LocalTime getLocalTimeStart(){
        return LocalTime.of(start[0],start[1]);
    }
    @JsonIgnore
    public LocalTime getLocalTimeEnd(){
        return LocalTime.of(end[0],end[1]);
    }
    public void setStartHours(int i){
        if(i<=24 && i >=0){
            this.start[0] = i;
        }
    }
    public void setStartMinutes(int i){
        if(i<=60 && i>=0){
            this.start[1] = i;
        }
    }
    public void setEndHours(int i){
        if(i<=24 && i >=0){
            this.end[0] = i;
        }
    }
    public void setEndMinutes(int i){
        if(i<=60 && i>=0){
            this.end[1] = i;
        }
    }
    public void setStart(int h, int m){
        setStartHours(h);
        setStartMinutes(m);

    }
    public void setEnd(int h, int m){
        setEndHours(h);
        setEndMinutes(m);
    }
    public int[] getStart(){
        return this.start;
    }
    public int[] getEnd(){
        return this.end;
    }
}
