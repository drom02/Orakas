package Orakas.Vacations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Vacation {

    private LocalDate start;
    private LocalDate end;
    boolean removesWorkDays;
@JsonCreator
public Vacation(@JsonProperty("start" )LocalDate start, @JsonProperty("end" )LocalDate end, @JsonProperty("removesWorkDays" )boolean removesWorkDays){
        setStart(start);
        setEnd(end);
        setRemovesWorkDays(removesWorkDays);
    }
    public boolean isRemovesWorkDays() {
        return removesWorkDays;
    }
    public void setRemovesWorkDays(boolean removesWorkDays) {
        this.removesWorkDays = removesWorkDays;
    }
    public LocalDate getStart() {
        return start;
    }
    public void setStart(LocalDate start) {
        this.start = start;
    }
    public LocalDate getEnd() {
        return end;
    }
    public void setEnd(LocalDate end) {
        this.end = end;
    }
    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (!(o instanceof Vacation)) {
            return false;
        }
        Vacation a = (Vacation) o;
        if(this.getStart().equals(a.getStart()) && this.getEnd().equals(a.getEnd())){
            return true;
        }else{
            return false;
        }
    }
}
