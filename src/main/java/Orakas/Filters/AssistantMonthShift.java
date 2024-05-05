package Orakas.Filters;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssistantMonthShift {
    private UUID assistantID;
    private long workedHours;
    private int lastDayInWork;

    public LocalDateTime getLastShift() {
        return lastShift;
    }

    public void setLastShift(LocalDateTime lastShift) {
        this.lastShift = lastShift;
    }

    private LocalDateTime lastShift;

    public AssistantMonthShift(UUID id){
        this.setAssistantID(id);

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

    public int getLastDayInWork() {
        return lastDayInWork;
    }

    public void setLastDayInWork(int lastDayInWork) {
        this.lastDayInWork = lastDayInWork;
    }
}
