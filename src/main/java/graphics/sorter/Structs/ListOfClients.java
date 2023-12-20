package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Assistant;
import graphics.sorter.Client;

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
}
