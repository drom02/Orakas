package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Assistant;
import graphics.sorter.Location;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ServiceInterval {



    private Assistant overseeingAssistant;
    private LocalDateTime start;
    private LocalDateTime end;
    private Location location;
    private boolean isNotRequired;

    private String comment;
//Přidat truncate form
    /*
@JsonCreator
     ServiceInterval(@JsonProperty("start")LocalTime start,@JsonProperty("end") LocalTime end){
        this.start = start;
        this.end = end;
    }

     */
    @JsonCreator
    public ServiceInterval(@JsonProperty("start") LocalDateTime start, @JsonProperty("end") LocalDateTime end, @JsonProperty("overseeingAssistant") Assistant overseeingAssistant, @JsonProperty("comment") String comment, @JsonProperty("isNotRequired") boolean isNotRequired){
        this.start = start;
        this.end = end;
        this.overseeingAssistant = overseeingAssistant != null ? overseeingAssistant : null;
        this.comment = comment == null ? new String() : comment;
        setNotRequired(isNotRequired);

    }
    public Assistant getOverseeingAssistant() {
        return overseeingAssistant;
    }

    public void setOverseeingAssistant(Assistant overseeingAssistant) {
        this.overseeingAssistant = overseeingAssistant;
    }
    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setEnd(LocalDateTime end) {

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
    public boolean getIsNotRequired() {
        return isNotRequired;
    }

    public void setNotRequired(boolean notRequired) {
        isNotRequired = notRequired;
    }
    @JsonIgnore
    public long getIntervalLength(){
        long hoursBetween = ChronoUnit.HOURS.between(start, end);
        return  Math.abs(hoursBetween);
    }
}
