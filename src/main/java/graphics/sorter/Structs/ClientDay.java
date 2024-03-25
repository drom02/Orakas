package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Assistant;
import graphics.sorter.Database;
import graphics.sorter.Location;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        
        ServiceInterval def = new ServiceInterval(defStarTime, defEndTime, null, null,null,false,false,location.getID());
        this.dayIntervalList.add(def);
        setDay(dayI);
        this.month = monthI;
        this.year = yearI;
        this.location = location;
        this.isMerged = ismerged;
        setDayStatus(isDay);
        setClient(client);
    }
    public void addInterval(LocalDateTime startNew,LocalDateTime endNew) {
        ClientDay day = this;
        ArrayList<ServiceInterval> toBeRemoved = new ArrayList<>();
        ArrayList<ServiceInterval> toBeResized = new ArrayList<>();
        boolean alreadyExists = false;
        for (ServiceInterval s : day.getDayIntervalList()) {
            LocalDateTime start = s.getStart();
            LocalDateTime end = s.getEnd();
            if (start.isEqual(startNew) && end.isEqual(endNew)) {
                return;
            }
            if ((startNew.isAfter(start) || startNew.isEqual(start)) && (startNew.isBefore(end) || startNew.isEqual(end)) ||
                    (endNew.isAfter(start) || endNew.isEqual(start)) && (endNew.isBefore(end) || endNew.isEqual(end))) {
                toBeResized.add(s);
            }
            if ((start.isAfter(startNew) && end.isBefore(endNew)) || start.isEqual(startNew) && end.isBefore(endNew) || start.isAfter(startNew) && end.isEqual(endNew)) {
                //   System.out.println("toBeRemoved");
                toBeRemoved.add(s);
            }
        }
        for (ServiceInterval serv : toBeRemoved) {
            Assistant as = serv.getOverseeingAssistant();
            day.getDayIntervalList().remove(serv);
            if (day.getDayIntervalList().isEmpty()) {
                day.getDayIntervalList().add(new ServiceInterval(startNew
                        , endNew, as, null, null, false, false, day.getLocation().getID()));
                if (day.getDayIntervalList().getFirst().getStart().isEqual(startNew) || day.getDayIntervalList().getLast().getEnd().isEqual(endNew)) {
                    System.out.println("Is empty");
                }
                return;
            }
        }
        for (ServiceInterval s : toBeResized) {
            LocalDateTime start = s.getStart();
            LocalDateTime end = s.getEnd();
            if ((startNew.isAfter(start) && startNew.isBefore(end)) & (endNew.isAfter(end) || endNew.isEqual(end))) {
                s.setEnd(startNew);
                System.out.println("Type 1");
            } else if ((startNew.isBefore(start) || startNew.isEqual(start)) & (endNew.isAfter(start) || endNew.isEqual(start) && endNew.isBefore(end) || endNew.isEqual(end))) {
                s.setStart(endNew);
                System.out.println("Type 2");
            } else if(start.isBefore(startNew) && end.isAfter(endNew)) {
                LocalDateTime temp = s.getEnd();
                s.setEnd(startNew);
                day.getDayIntervalList().add(new ServiceInterval(endNew
                        , temp, s.getOverseeingAssistant(), null,null, false, false,day.getLocation().getID()));
                System.out.println("Type 3");
                break;
            }else{
                System.out.println("Type Error");
                System.out.println("start " + start);
                System.out.println("end " + end);
                System.out.println("newStart " + startNew);
                System.out.println("newEnd " + endNew);
            }
        }
        day.getDayIntervalList().add(new ServiceInterval(startNew
                , endNew, day.getDayIntervalList().getFirst().getOverseeingAssistant(), null,null, false, false,day.getLocation().getID()));
        if(day.getDayIntervalList().getFirst().getStart().isEqual(startNew) || day.getDayIntervalList().getLast().getEnd().isEqual(endNew)){
        }
       // day.getDayIntervalListUsefull().get(0).setComment("Testing save logic");
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
        return ChronoUnit.MINUTES.between(dayIntervalList.getFirst().getStart(),dayIntervalList.getLast().getEnd());
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
