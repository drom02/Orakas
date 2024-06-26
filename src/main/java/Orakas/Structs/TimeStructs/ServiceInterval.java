package Orakas.Structs.TimeStructs;

import Orakas.Humans.Assistant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class ServiceInterval {
    private Assistant overseeingAssistant;
    private LocalDateTime start;
    private LocalDateTime end;
    private UUID location;
    private UUID assignedAssistant;
    private boolean isNotRequired;
    private boolean isMerged;
    private String comment;

    public boolean isRequiresDriver() {
        return requiresDriver;
    }

    public void setRequiresDriver(boolean requiresDriver) {
        this.requiresDriver = requiresDriver;
    }

    private boolean requiresDriver;
//Přidat truncate form
    /*
@JsonCreator
     ServiceInterval(@JsonProperty("start")LocalTime start,@JsonProperty("end") LocalTime end){
        this.start = start;
        this.end = end;
    }

     */
    @JsonCreator
    public ServiceInterval(@JsonProperty("start") LocalDateTime start,
                           @JsonProperty("end") LocalDateTime end,
                           @JsonProperty("overseeingAssistant") Assistant overseeingAssistant,
                           @JsonProperty("assignedAssistant") UUID assignedAssistant,
                           @JsonProperty("comment") String comment,
                           @JsonProperty("isNotRequired") boolean isNotRequired,
                           @JsonProperty("isMerged") boolean isMerged,
                           @JsonProperty("location") UUID location,
                             @JsonProperty("requiresDriver") boolean requiresDriver){
        setStart(start);
        this.end = end;
        this.overseeingAssistant = overseeingAssistant != null ? overseeingAssistant : null;
        this.comment = comment == null ? new String() : comment;
        setNotRequired(isNotRequired);
        setMerged(isMerged);
        setAssignedAssistant(assignedAssistant);
        setLocation(location);
        setRequiresDriver(requiresDriver);

    }

   public  long serviceIntervalLength(){
            return ChronoUnit.MINUTES.between(this.getStart(),this.getEnd());
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
    public boolean isMerged() {
        return isMerged;
    }

    public void setMerged(boolean merged) {
        isMerged = merged;
    }

    public UUID getLocation() {
        return location;
    }

    public void setLocation(UUID location) {
        this.location = location;
    }
    public boolean getIsNotRequired() {
        return isNotRequired;
    }

    public void setNotRequired(boolean notRequired) {
        isNotRequired = notRequired;
    }
    public UUID getAssignedAssistant() {
        return assignedAssistant;
    }

    public void setAssignedAssistant(UUID assignedAssistant) {
        this.assignedAssistant = assignedAssistant;
    }
    @JsonIgnore
    public long getIntervalLength(){
        long hoursBetween = ChronoUnit.MINUTES.between(start, end);
        return  Math.abs(hoursBetween);
    }
    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServiceInterval)) {
            return false;
        }
        ServiceInterval a = (ServiceInterval) o;
        if(this.getStart().equals(a.getStart()) && this.getEnd().equals(a.getEnd())){
            return true;
        }else{
            return false;
        }
    }
}
