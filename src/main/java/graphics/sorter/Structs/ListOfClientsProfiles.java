package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import graphics.sorter.Client;
import graphics.sorter.ClientProfile;
import graphics.sorter.JsonManip;

import java.io.IOException;
import java.util.ArrayList;

public class ListOfClientsProfiles implements Saveable{


    public ArrayList<ClientProfile> getClientList() {
        return clientList;
    }

    public void setClientList(ArrayList<ClientProfile> clientList) {
        this.clientList = clientList;
    }

    public ArrayList<ClientProfile> clientList;

    @JsonCreator
    public ListOfClientsProfiles() {
        this.clientList = new ArrayList<ClientProfile>();
    }
    public void createNew(JsonManip map) throws IOException {
        ListOfClientsProfiles los = new ListOfClientsProfiles();
        map.saveClientInfo(los);
    }
}
