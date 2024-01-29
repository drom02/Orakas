package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Assistant;

import java.time.LocalTime;

public class ServiceInterval {



    private Assistant overseeingAssistant;
    private LocalTime start;
    private LocalTime end;
    private Location location;
//Přidat truncate form
    /*
@JsonCreator
     ServiceInterval(@JsonProperty("start")LocalTime start,@JsonProperty("end") LocalTime end){
        this.start = start;
        this.end = end;
    }

     */
    @JsonCreator
    ServiceInterval(@JsonProperty("start")LocalTime start,@JsonProperty("end") LocalTime end,@JsonProperty("overseeingAssistant") Assistant overseeingAssistant){
        this.start = start;
        this.end = end;
        this.overseeingAssistant = overseeingAssistant != null ? overseeingAssistant : null;

    }
    public Assistant getOverseeingAssistant() {
        return overseeingAssistant;
    }

    public void setOverseeingAssistant(Assistant overseeingAssistant) {
        this.overseeingAssistant = overseeingAssistant;
    }
    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
            if(end.compareTo(getStart())>= 0){
                this.end = end ;
            }else{
                throw new ArithmeticException("Interval nemůže skončit předtím než začne");
            }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
