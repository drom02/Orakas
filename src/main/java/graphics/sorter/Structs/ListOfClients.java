package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Assistant;
import graphics.sorter.Client;
import graphics.sorter.ClientProfile;

import java.util.ArrayList;

public class ListOfClients {


    public ArrayList<Client> getClientList() {
        return clientList;
    }

    public void setClientList(ArrayList<Client> clientList) {
        this.clientList = clientList;
    }

    public ArrayList<Client> clientList;

    @JsonCreator
    public ListOfClients() {
        this.clientList = new ArrayList<Client>();
    }


    public ListOfClientsProfiles convertToListOfClientProfiles(){
       ListOfClientsProfiles output = new ListOfClientsProfiles();
       if(!clientList.isEmpty()){
           for (Client cl : clientList  ){
                output.getClientList().add( cl.convertToClientProfile());
           }
       }
       return  output;
    }
}
