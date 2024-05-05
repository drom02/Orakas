package Orakas.Filters;

import java.time.LocalDateTime;
/*
Data class used for subsections of workShift.
 */
public class WorkShiftSubInterval {
    public LocalDateTime getStartOf() {
        return startOf;
    }

    public void setStartOf(LocalDateTime startOf) {
        this.startOf = startOf;
    }

    public LocalDateTime getEndOf() {
        return endOf;
    }

    public void setEndOf(LocalDateTime endOf) {
        this.endOf = endOf;
    }

    LocalDateTime startOf;
    LocalDateTime endOf;

    WorkShiftSubInterval(LocalDateTime start, LocalDateTime end){
        setStartOf(start);
        setEndOf(end);
    }
}
