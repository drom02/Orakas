package Orakas;

import Orakas.Database.Database;
import Orakas.Humans.Client;
import Orakas.Structs.Availability.AvailableAssistants;
import Orakas.Structs.TimeStructs.ClientDay;
import Orakas.Structs.ListOfClients;

import java.io.IOException;
import java.util.ArrayList;

public class AttachAssistants {

     private AvailableAssistants availableAssistants;
     private ListOfClients listOfClients;
    //public void a(AvailableAssistants availableAssistants, ){

   // }

    public AttachAssistants() throws IOException {
            initialize();


    }
    public void initialize() throws IOException {
        Settings set = Settings.getSettings();
        try {
            availableAssistants = Database.loadAssistantAvailability(set.getCurrentYear(), set.getCurrentMonth());
        }catch(Exception e){
            JsonManip.generateEmptyState(set);
        }

       listOfClients = Database.loadFullClients(set.getCurrentYear(),set.getCurrentMonth());
       extractClientsForDay(1);
    }
    private ArrayList<ClientDay> extractClientsForDay(int day ){
        ArrayList<ClientDay> extractedDays = new ArrayList<ClientDay>();
        for(Client cl : listOfClients.getActiveClients()){

            extractedDays.add(cl.getClientsMonth().getClientDaysInMonth().get(day));
        }
        return extractedDays;
    }
}
