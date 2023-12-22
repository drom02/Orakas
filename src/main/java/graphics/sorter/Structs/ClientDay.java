package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;

public class ClientDay {
    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private Integer day;
    private Month month;
    private  Integer year;
    private Location location;

    public ServinceIntervalArrayList getDayIntervalList() {
        return dayIntervalList;
    }

    public void setDayIntervalList(ServinceIntervalArrayList dayIntervalList) {
        this.dayIntervalList = dayIntervalList;
    }

    private ServinceIntervalArrayList dayIntervalList = new ServinceIntervalArrayList();

    public void test(){
        ServiceInterval test2 = new ServiceInterval(LocalTime.of(10,00,00), LocalTime.of(14,00,00));
        ServiceInterval test1 = new ServiceInterval(LocalTime.of(15,00,00), LocalTime.of(16,00,00));
        ServiceInterval test3 = new ServiceInterval(LocalTime.of(8,00,00), LocalTime.of(9,00,00));
        ServiceInterval test4 = new ServiceInterval(LocalTime.of(5,00,00), LocalTime.of(6,00,00));
        dayIntervalList.add(test1);
        dayIntervalList.add(test2);
        dayIntervalList.add(test3);
        dayIntervalList.add(test4);
        System.out.println("test");

    }
    @JsonCreator
     ClientDay(@JsonProperty("dayI")Integer dayI,@JsonProperty("monthI")Month monthI,@JsonProperty("year")Integer yearI, @JsonProperty("defStarTime")int[] defStarTime,@JsonProperty("defEndTime")int[] defEndTime ){
        if(defStarTime == null){
            defStarTime  = new int[]{8, 30};
        }
        if(defEndTime == null){
            defEndTime  = new int[]{20,30};
        }
        ServiceInterval def = new ServiceInterval(LocalTime.of(defStarTime[0],defStarTime[1],00), LocalTime.of(defEndTime[0],defEndTime[1],00));
        this.dayIntervalList.add(def);
        this.day = dayI;
        this.month = monthI;
        this.year = yearI;
    }


}
