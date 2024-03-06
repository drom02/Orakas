package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Assistant;
import graphics.sorter.Client;
import graphics.sorter.ClientProfile;
import graphics.sorter.JsonManip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ListOfClientsProfiles implements Saveable{

    @JsonIgnore
    public ArrayList<ClientProfile> getClientList() {

        return clientList.stream().filter(f->f.getActivityStatus()==true).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<ClientProfile> getFullClientList() {
        return clientList;
    }

    public void setFullClientList(ArrayList<ClientProfile> clientList) {
        this.clientList = clientList;
    }

    private ArrayList<ClientProfile> clientList;

    @JsonCreator
    public ListOfClientsProfiles(@JsonProperty("clientList") ArrayList<ClientProfile> clientList) {
        this.clientList = clientList;
    }
    public void createNew(JsonManip map) throws IOException {
        ListOfClientsProfiles los = new ListOfClientsProfiles(null);
        map.saveClientInfo(los);
    }
}
