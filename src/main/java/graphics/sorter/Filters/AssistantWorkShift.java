package graphics.sorter.Filters;

import graphics.sorter.Structs.ClientDay;
import graphics.sorter.Structs.ServiceInterval;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class AssistantWorkShift {
    private UUID assistantID;
    private long workedHours;
    private int day;
    private int month;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean type;


    public AssistantWorkShift(UUID id, ClientDay cd){
        setAssistantID(id);
        setUpFromDay(cd);
    }
    public AssistantWorkShift(){

    }
    private void setUpFromDay(ClientDay cd){
        setStart(cd.getDayIntervalList().getFirst().getStart());
        setEnd(cd.getDayIntervalList().getLast().getEnd());
        setDay(cd.getDay());
        setMonth(cd.getMonth().getValue());
        setWorkedHours(calculateWorkLength(cd));
        setType(cd.getDayStatus());
    }
    public void setUpFromInterval(ServiceInterval si, ClientDay cd, UUID id){
        setAssistantID(id);
        if(getStart() == null ||si.getStart().isBefore(getStart())){
            setStart(si.getStart());
        }
        if(getEnd()== null ||si.getEnd().isAfter(getEnd())){
            setEnd(si.getEnd());
        }
        if(getMonth() !=0){
            setDay(cd.getDay());
            setMonth(cd.getMonth().getValue());
        }
        setDay(cd.getDay());
        setType(cd.getDayStatus());
        setWorkedHours(workedHours+serviceIntervalLength(si));
    }
    private long calculateWorkLength(ClientDay cd){
        long length =0;
        for(ServiceInterval s :cd.getDayIntervalList()){
            if(s.getIsNotRequired() == false){
                length = length + ChronoUnit.HOURS.between(s.getStart(),s.getEnd());
            }
        }
        return length;
    }
    private long serviceIntervalLength(ServiceInterval s){
        return ChronoUnit.HOURS.between(s.getStart(),s.getEnd());
    }
    public UUID getAssistantID() {
        return assistantID;
    }

    public void setAssistantID(UUID assistantID) {
        this.assistantID = assistantID;
    }

    public long getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(long workedHours) {
        this.workedHours = workedHours;
    }
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
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

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}