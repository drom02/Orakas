package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import graphics.sorter.Client;
import graphics.sorter.ClientProfile;

import java.util.ArrayList;

public class ListOfClientsProfiles {


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
}
