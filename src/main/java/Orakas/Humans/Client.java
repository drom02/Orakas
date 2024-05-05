package Orakas.Humans;

import Orakas.Location;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import Orakas.Structs.TimeStructs.ClientMonth;

import java.util.UUID;
/*
Class whose instances represent individual clients in specific month. Apart from information about client also contains
information about client requirements for specific month.
 */
public class Client extends ClientProfile {


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
    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        Client a = (Client) o;
        if(this.getID().equals(a.getID())){
            return true;
        }else{
            return false;
        }
    }
    public void setClientsMonth(ClientMonth clientsMonth) {
        this.clientsMonth = clientsMonth;
    }
    public ClientProfile convertToClientProfile(){
        ClientProfile clP = new ClientProfile(this.getID(), this.getActivityStatus(),this.getName(), this.getSurname(), this.getHomeLocation(),this.getComment());
        return  clP;
    }
}
