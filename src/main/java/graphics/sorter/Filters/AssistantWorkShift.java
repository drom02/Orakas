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
    private void setUpFromDay(ClientDay cd){
        setStart(cd.getDayIntervalList().getFirst().getStart());
        setEnd(cd.getDayIntervalList().getLast().getEnd());
        setDay(cd.getDay());
        setMonth(cd.getMonth().getValue());
        setWorkedHours(calculateWorkLength(cd));
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