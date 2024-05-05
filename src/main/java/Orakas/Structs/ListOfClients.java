package Orakas.Structs;

import Orakas.Humans.Client;
import Orakas.Humans.ClientProfile;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
/*
Class used for manipulations with multiple clients at the same time.
 */
public class ListOfClients {


    public ArrayList<Client> getClientList() {
        return clientList;
    }

    public void setClientList(ArrayList<Client> clientList) {
        this.clientList = clientList;
    }

    private ArrayList<Client> clientList;

    @JsonCreator
    public ListOfClients() {
        this.clientList = new ArrayList<Client>();
    }
    public ArrayList<Client> getActiveClients(){
        ArrayList<Client> output = new ArrayList<>();
        for(Client c : getClientList()){
            if(c.getActivityStatus()){
                output.add(c);
            }
        }
        return  output;
    }


    public ListOfClientsProfiles convertToListOfClientProfiles(){
       ListOfClientsProfiles output = new ListOfClientsProfiles(new ArrayList<ClientProfile>());
       if(!clientList.isEmpty()){
           for (Client cl : clientList  ){
                output.getFullClientList().add( cl.convertToClientProfile());
           }
       }
       return  output;
    }
}
