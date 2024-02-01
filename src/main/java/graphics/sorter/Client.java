package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Structs.ClientMonth;

import java.util.UUID;

public class Client extends ClientProfile{


    private ClientMonth clientsMonth;
    private Location loc;
    @JsonCreator
    public Client(@JsonProperty("ID") UUID ID, @JsonProperty("name")String name, @JsonProperty("surname")String surname, @JsonProperty("clientMonth")ClientMonth clientMonth, @JsonProperty("homeLocation")Location homeLocation){
        super(ID,name, surname, homeLocation);
        setClientsMonth(clientMonth);
    }

    public ClientMonth getClientsMonth() {
        return clientsMonth;
    }

    public void setClientsMonth(ClientMonth clientsMonth) {
        this.clientsMonth = clientsMonth;
    }
    public ClientProfile convertToClientProfile(){
        ClientProfile clP = new ClientProfile(this.getID(),this.getName(), this.getSurname(), this.getHomeLocation());
        return  clP;
    }
}
