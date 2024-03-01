package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Structs.ClientMonth;

import java.util.UUID;

public class Client extends ClientProfile{


    private ClientMonth clientsMonth;
    private Location loc;
    @JsonCreator
    public Client(@JsonProperty("ID") UUID ID,
                  @JsonProperty("status") boolean status,
                  @JsonProperty("name")String name,
                  @JsonProperty("surname")String surname,
                  @JsonProperty("clientMonth")ClientMonth clientMonth,
                  @JsonProperty("homeLocation")Location homeLocation,
                  @JsonProperty("comment")String comment){
        super(ID,status,name, surname, homeLocation,comment);
        setClientsMonth(clientMonth);
    }

    public ClientMonth getClientsMonth() {
        return clientsMonth;
    }

    public void setClientsMonth(ClientMonth clientsMonth) {
        this.clientsMonth = clientsMonth;
    }
    public ClientProfile convertToClientProfile(){
        ClientProfile clP = new ClientProfile(this.getID(), this.getActivityStatus(),this.getName(), this.getSurname(), this.getHomeLocation(),this.getComment());
        return  clP;
    }
}
