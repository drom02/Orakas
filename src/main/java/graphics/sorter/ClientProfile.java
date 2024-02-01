package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Structs.ClientMonth;

import java.util.UUID;

public class ClientProfile extends Human {



    private String ClientId;

    public Location getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }

    private Location homeLocation;

    @JsonCreator
    public ClientProfile(@JsonProperty("ID") UUID ID, @JsonProperty("name")String name, @JsonProperty("surname")String surname, @JsonProperty("homeLocation")Location homeLocation){
        setID(ID);
        setName(name);
        setSurname(surname);
        setHomeLocation(homeLocation);
    }
    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public Client convertToClient(ClientMonth assignedMonth){
        Client output = new Client(getID(),getName(), getSurname(),assignedMonth, getHomeLocation());
        return output;
    }

}
