package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Client;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ClientMonth {


    private String clientId;
    private ArrayList<ClientDay> clientDaysInMonth = new ArrayList<ClientDay>();
    private ArrayList<ClientDay> clientNightsInMonth = new ArrayList<ClientDay>();

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

    private Month mon;
    private int year;

    @JsonCreator
   public  ClientMonth(@JsonProperty("mon")Month mon,@JsonProperty("year") int year,@JsonProperty("clientId") String clientId){
        GregorianCalendar cal = new GregorianCalendar();
        this.clientId = clientId;
        this.mon = mon;
        this.year = year;
      // this.clientOwningMonth = cl;
        int i = 1;
        while(i <=+
                mon.length(cal.isLeapYear(year))){
            ClientDay newDay = new ClientDay(i,mon, year, new int[]{8, 30},new int[]{20, 30});
            ClientDay newNight = new ClientDay(i,mon,year, new int[]{20, 30},new int[]{8, 30});
            clientDaysInMonth.add(newDay);
            clientNightsInMonth.add(newNight);
            i++;
        }
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
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
