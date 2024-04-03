package Orakas.Filters;

import Orakas.Structs.TimeStructs.ClientDay;
import Orakas.Structs.TimeStructs.ServiceInterval;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

public class AssistantWorkShift {
    private UUID assistantID;
    private long workedMinutes;
    private int day;
    private int month;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean type;



    private ArrayList<WorkShiftSubInterval> workedIntervals = new ArrayList<>();


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
        setWorkedMinutes(calculateWorkLength(cd));
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
        workedIntervals.add(new WorkShiftSubInterval(si.getStart(),si.getEnd()));
        setDay(cd.getDay());
        setType(cd.getDayStatus());
        setWorkedMinutes(workedMinutes +serviceIntervalLength(si));
    }
    private long calculateWorkLength(ClientDay cd){
        long length =0;
        for(ServiceInterval s :cd.getDayIntervalList()){
            if(s.getIsNotRequired() == false){
                length = length + ChronoUnit.MINUTES.between(s.getStart(),s.getEnd());
            }
        }
        return length;
    }
    private long serviceIntervalLength(ServiceInterval s){
        return ChronoUnit.MINUTES.between(s.getStart(),s.getEnd());
    }
    public UUID getAssistantID() {
        return assistantID;
    }

    public void setAssistantID(UUID assistantID) {
        this.assistantID = assistantID;
    }

    public long getWorkedMinutes() {
        return workedMinutes;
    }

    public void setWorkedMinutes(long workedMinutes) {
        this.workedMinutes = workedMinutes;
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
    public ArrayList<WorkShiftSubInterval> getWorkedIntervals() {
        return workedIntervals;
    }

    public void setWorkedIntervals(ArrayList<WorkShiftSubInterval> workedIntervals) {
        this.workedIntervals = workedIntervals;
    }
}