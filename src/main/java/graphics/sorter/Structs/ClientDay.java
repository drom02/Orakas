package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Location;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class ClientDay {


    private UUID client;
    private boolean isMerged = false;
    private boolean isDay = false;
    private Integer day;
    private Month month;
    private  Integer year;
    private Location location;
    private LocalDateTime defStarTime;
    private LocalDateTime defEndTime;
    private ServiceIntervalArrayList dayIntervalList = new ServiceIntervalArrayList();

    public void test(){
        /*
        ServiceInterval test2 = new ServiceInterval(LocalTime.of(10,00,00), LocalTime.of(14,00,00), null, null,false);
        ServiceInterval test1 = new ServiceInterval(LocalTime.of(15,00,00), LocalTime.of(16,00,00), null, null,false);
        ServiceInterval test3 = new ServiceInterval(LocalTime.of(8,00,00), LocalTime.of(9,00,00), null, null,false);
        ServiceInterval test4 = new ServiceInterval(LocalTime.of(5,00,00), LocalTime.of(6,00,00), null, null,false);
        dayIntervalList.add(test1);
        dayIntervalList.add(test2);
        dayIntervalList.add(test3);
        dayIntervalList.add(test4);
        System.out.println("test");
         */



    }
    @JsonCreator
     public ClientDay(@JsonProperty("client")UUID client,@JsonProperty("dayI")Integer dayI,
               @JsonProperty("monthI")Month monthI, @JsonProperty("year")Integer yearI,
               @JsonProperty("defStarTime") LocalDateTime defStarTime, @JsonProperty("defEndTime")LocalDateTime defEndTime,
               @JsonProperty("Location")Location location, @JsonProperty("isMerged")boolean ismerged, @JsonProperty("isDay")boolean isDay){
        
        ServiceInterval def = new ServiceInterval(defStarTime, defEndTime, null, null,false,false);
        this.dayIntervalList.add(def);
        setDay(dayI);
        this.month = monthI;
        this.year = yearI;
        this.location = location;
        this.isMerged = ismerged;
        setDayStatus(isDay);
        setClient(client);
    }

    public boolean isMerged() {
        return isMerged;
    }

    public void setMerged(boolean merged) {
        isMerged = merged;
    }

    public LocalDateTime getDefStarTime() {
        return defStarTime;
    }

    public void setDefStarTime(LocalDateTime defStarTime) {
        this.defStarTime = defStarTime;
    }

    public LocalDateTime getDefEndTime() {
        return defEndTime;
    }

    public void setDefEndTime(LocalDateTime defEndTime) {
        this.defEndTime = defEndTime;
    }
    public boolean getDayStatus() {
        return isDay;
    }

    public void setDayStatus(boolean day) {
        isDay = day;
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
    public long shiftLength(){
        return ChronoUnit.HOURS.between(dayIntervalList.getFirst().getStart(),dayIntervalList.getLast().getEnd());
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
    public UUID getClient() {
        return client;
    }

    public void setClient(UUID client) {
        this.client = client;
    }
}
