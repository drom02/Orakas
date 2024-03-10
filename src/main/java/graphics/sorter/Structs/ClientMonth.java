package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Client;
import graphics.sorter.JsonManip;
import graphics.sorter.Location;
import graphics.sorter.Settings;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public class ClientMonth {


    private UUID clientId;
    private Month mon;
    private int year;
    private ArrayList<ClientDay> clientDaysInMonth = new ArrayList<ClientDay>();
    private ArrayList<ClientDay> clientNightsInMonth = new ArrayList<ClientDay>();
    private JsonManip jsoM = JsonManip.getJsonManip();

    public Month getMon() {
        return mon;
    }

    public void setMon(Month mon) {
        this.mon = mon;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }



    @JsonCreator
   public  ClientMonth(@JsonProperty("mon")Month mon,@JsonProperty("year") int year,@JsonProperty("clientId") UUID clientId,@JsonProperty("location") Location location ){
        GregorianCalendar cal = new GregorianCalendar();
        this.clientId = clientId;
        this.mon = mon;
        this.year = year;
      // this.clientOwningMonth = cl;

        int dayDefHour = 8;
        int dayDefMin = 30;
        int nightDefHour = 20;
        int nightDefMin = 30;
        ClientDay  newDay0 = new ClientDay(clientId,1,mon, year, LocalDateTime.of(year,mon.getValue(),1,dayDefHour,dayDefMin),LocalDateTime.of(year,mon.getValue(),1,nightDefHour,nightDefMin), location,false, true);
        ClientDay  newNight0;
        if(mon.getValue()>1){
              newNight0 = new ClientDay(clientId,1,mon, year, LocalDateTime.of(year,mon.getValue()-1,Month.of(mon.getValue()-1).length(Year.isLeap(year)),nightDefHour,nightDefMin),LocalDateTime.of(year,mon.getValue(),1,dayDefHour,dayDefMin), location,false,false);
        }else{
              newNight0 = new ClientDay(clientId,1,mon, year, LocalDateTime.of(year-1,12,Month.of(mon.getValue()-1).length(Year.isLeap(year)),nightDefHour,nightDefMin),LocalDateTime.of(year,mon.getValue(),1,dayDefHour,dayDefMin), location,false,false);
        }
       // clientDaysInMonth.add(newDay0);
        //clientNightsInMonth.add(newNight0);
        int i = 1;
        while(i <= mon.length(cal.isLeapYear(year))-1){
            ClientDay newDay = new ClientDay(clientId,i,mon, year,LocalDateTime.of(year,mon.getValue(),i,dayDefHour,dayDefMin),LocalDateTime.of(year,mon.getValue(),i,nightDefHour,nightDefMin), location,false,true);
            ClientDay newNight = new ClientDay(clientId,i,mon, year,LocalDateTime.of(year,mon.getValue(),i,nightDefHour,nightDefMin),LocalDateTime.of(year,mon.getValue(),i+1,dayDefHour,dayDefMin), location,false,false);
            clientDaysInMonth.add(newDay);
            clientNightsInMonth.add(newNight);
            i++;
        }
        ClientDay  newDayLast = new ClientDay(clientId,mon.length(cal.isLeapYear(year)),mon, year, LocalDateTime.of(year,mon.getValue(),mon.length(cal.isLeapYear(year)),dayDefHour,dayDefMin),LocalDateTime.of(year,mon.getValue(),mon.length(cal.isLeapYear(year)),nightDefHour,nightDefMin), location,false,true);
        ClientDay  newNightLast;
        if(mon.getValue() != 12){
            newNightLast = new ClientDay(clientId,mon.length(cal.isLeapYear(year)),mon, year, LocalDateTime.of(year,mon.getValue(),mon.length(cal.isLeapYear(year)),nightDefHour,nightDefMin),LocalDateTime.of(year,mon.getValue()+1,1,dayDefHour,dayDefMin), location,false,false);
        }else{
            newNightLast = new ClientDay(clientId,mon.length(cal.isLeapYear(year)),mon, year, LocalDateTime.of(year,mon,mon.length(cal.isLeapYear(year)),nightDefHour,nightDefMin),LocalDateTime.of(year+1,1,1,dayDefHour,dayDefMin), location,false,false);
        }
        clientDaysInMonth.add(newDayLast);
        clientNightsInMonth.add(newNightLast);
    }
    @JsonIgnore
    public ClientMonth(Month mon,int year, UUID clientId,ArrayList<ClientDay> days, ArrayList<ClientDay> nights){
        this.clientId = clientId;
        this.mon = mon;
        this.year = year;
        setClientDaysInMonth(days);
        setClientNightsInMonth(nights);
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }
    public ArrayList<ClientDay> getClientDaysInMonth() {
        return clientDaysInMonth;
    }

    public void setClientDaysInMonth(ArrayList<ClientDay> clientDaysInMonth) {
        this.clientDaysInMonth = clientDaysInMonth;
    }
    public ArrayList<ClientDay> getClientNightsInMonth() {
        return clientNightsInMonth;
    }

    public void setClientNightsInMonth(ArrayList<ClientDay> clientNightsInMonth) {
        this.clientNightsInMonth = clientNightsInMonth;
    }
}
