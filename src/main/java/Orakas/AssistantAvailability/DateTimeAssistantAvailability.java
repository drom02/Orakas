package Orakas.AssistantAvailability;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeAssistantAvailability {
    private LocalDateTime start;
   private LocalDateTime end;
   private AssistantAvailability assistantAvailability;
    private boolean isDay;

    public DateTimeAssistantAvailability(int year, int month, int day, boolean isDay,AssistantAvailability av){
        LocalDate date = LocalDate.of(year,month,day);
        setStart(createStart(date,isDay,av));
        setEnd(createEnd(date,isDay,av));
        setDay(isDay);
        setAssistantAvailability(av);
    }
    private LocalDateTime createStart(LocalDate date,boolean isDay,AssistantAvailability av){
        LocalTime localStart = av.getAvailability().getLocalTimeStart();
        if(isDay==false){
            if(localStart.isBefore(LocalTime.of(12,00,00))){
                return LocalDateTime.of(date.plusDays(1),localStart);
            }
        }
        return LocalDateTime.of(date,localStart);
    }
    private LocalDateTime createEnd(LocalDate date, boolean isDay,AssistantAvailability av){
        LocalTime localEnd = av.getAvailability().getLocalTimeEnd();
        if(isDay==false){
            if(localEnd.isBefore(LocalTime.of(12,00,00))){
                return LocalDateTime.of(date.plusDays(1),localEnd);
            }
        }
        return LocalDateTime.of(date,localEnd);
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
    public boolean isDay() {
        return isDay;
    }

    public void setDay(boolean day) {
        isDay = day;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public AssistantAvailability getAssistantAvailability() {
        return assistantAvailability;
    }

    public void setAssistantAvailability(AssistantAvailability assistantAvailability) {
        this.assistantAvailability = assistantAvailability;
    }
}
