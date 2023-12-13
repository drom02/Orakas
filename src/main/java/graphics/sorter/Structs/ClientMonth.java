package graphics.sorter.Structs;

import graphics.sorter.Client;

import java.time.Month;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ClientMonth {
    private Client clientOwningMonth;
    private ArrayList<ClientDay> clientDaysInMonth = new ArrayList<ClientDay>();

   public  ClientMonth(Month mon, Boolean leapYear, Client cl){
       this.clientOwningMonth = cl;
        int i = 0;
        while(i < mon.length(leapYear)){
            ClientDay newDay = new ClientDay(i,mon.getValue());
            clientDaysInMonth.add(newDay);
            i++;
        }
    }
    public Client getClientOwningMonth() {
        return clientOwningMonth;
    }

    public void setClientOwningMonth(Client clientOwningMonth) {
        this.clientOwningMonth = clientOwningMonth;
    }

    public ArrayList<ClientDay> getClientDaysInMonth() {
        return clientDaysInMonth;
    }

    public void setClientDaysInMonth(ArrayList<ClientDay> clientDaysInMonth) {
        this.clientDaysInMonth = clientDaysInMonth;
    }
}
