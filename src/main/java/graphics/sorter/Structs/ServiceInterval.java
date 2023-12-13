package graphics.sorter.Structs;

import java.time.LocalTime;

public class ServiceInterval {


    private LocalTime start;
    private LocalTime end;
    private Location location;
//Přidat truncate form
    public ServiceInterval(LocalTime start, LocalTime end){
        this.start = start;
        this.end = end;

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
