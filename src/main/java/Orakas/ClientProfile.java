package Orakas;

import Orakas.Humans.Client;
import Orakas.Humans.Human;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import Orakas.Structs.TimeStructs.ClientMonth;

import java.util.UUID;

public class ClientProfile extends Human {

    public Location getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }

    private Location homeLocation;

    @JsonCreator
    public ClientProfile(@JsonProperty("ID") UUID ID,
                         @JsonProperty("status") boolean status,
                         @JsonProperty("name")String name,
                         @JsonProperty("surname")String surname,
                         @JsonProperty("homeLocation")Location homeLocation,
                         @JsonProperty("comment")String comment){
        setID(ID);
        setActivityStatus(status);
        setName(name);
        setSurname(surname);
        setHomeLocation(homeLocation);
        setComment(comment);
    }
    public Client convertToClient(ClientMonth assignedMonth){
        Client output = new Client(getID(), getActivityStatus(), getName(), getSurname(),assignedMonth, getHomeLocation(),getComment());
        return output;
    }

}
