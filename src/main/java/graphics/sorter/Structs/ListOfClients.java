package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Assistant;

import java.util.ArrayList;

public class ListOfClients {

    public ArrayList<Assistant> getClientList() {
        return clientList;
    }

    public void setClientList(ArrayList<Assistant> clientList) {
        this.clientList = clientList;
    }

    public ArrayList<Assistant> clientList;

    @JsonCreator
    ListOfClients(@JsonProperty("assistantList") ArrayList<Assistant> assistantList) {
        this.clientList = assistantList;
    }
}
