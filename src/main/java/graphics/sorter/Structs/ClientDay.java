package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Location;

import java.time.LocalTime;
import java.time.Month;

public class ClientDay {

    public boolean isMerged = false;
    private Integer day;
    private Month month;
    private  Integer year;
    private Location location;
    private int[] defStarTime;
    private int[] defEndTime;
    private ServiceIntervalArrayList dayIntervalList = new ServiceIntervalArrayList();

    public void test(){
        ServiceInterval test2 = new ServiceInterval(LocalTime.of(10,00,00), LocalTime.of(14,00,00), null, null);
        ServiceInterval test1 = new ServiceInterval(LocalTime.of(15,00,00), LocalTime.of(16,00,00), null, null);
        ServiceInterval test3 = new ServiceInterval(LocalTime.of(8,00,00), LocalTime.of(9,00,00), null, null);
        ServiceInterval test4 = new ServiceInterval(LocalTime.of(5,00,00), LocalTime.of(6,00,00), null, null);
        dayIntervalList.add(test1);
        dayIntervalList.add(test2);
        dayIntervalList.add(test3);
        dayIntervalList.add(test4);
        System.out.println("test");

    }
    @JsonCreator
     ClientDay(@JsonProperty("dayI")Integer dayI,@JsonProperty("monthI")Month monthI,@JsonProperty("year")Integer yearI, @JsonProperty("defStarTime")int[] defStarTime,@JsonProperty("defEndTime")int[] defEndTime,@JsonProperty("Location")Location location,@JsonProperty("isMerged")boolean ismerged ){
        if(defStarTime == null){
            defStarTime  = new int[]{8, 30};
        }
        if(defEndTime == null){
            defEndTime  = new int[]{20,30};
        }
        ServiceInterval def = new ServiceInterval(LocalTime.of(defStarTime[0],defStarTime[1],00), LocalTime.of(defEndTime[0],defEndTime[1],00), null, null);
        this.dayIntervalList.add(def);
        this.day = dayI;
        this.month = monthI;
        this.year = yearI;
        this.location = location;
        this.isMerged = ismerged;
    }

    public boolean isMerged() {
        return isMerged;
    }

    public void setMerged(boolean merged) {
        isMerged = merged;
    }

    public int[] getDefStarTime() {
        return defStarTime;
    }

    public void setDefStarTime(int[] defStarTime) {
        this.defStarTime = defStarTime;
    }

    public int[] getDefEndTime() {
        return defEndTime;
    }

    public void setDefEndTime(int[] defEndTime) {
        this.defEndTime = defEndTime;
    }
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

    @JsonIgnore
    public ServiceIntervalArrayList getDayIntervalListUsefull() {
        ServiceIntervalArrayList out = new ServiceIntervalArrayList();
        for(ServiceInterval serv : dayIntervalList){
            if(serv.getIsNotRequired()==false){
                out.add(serv);
            }

        }
        return out;
    }
    public ServiceIntervalArrayList getDayIntervalList() {

        return dayIntervalList;
    }


    public void setDayIntervalList(ServiceIntervalArrayList dayIntervalList) {
        this.dayIntervalList = dayIntervalList;
    }
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
