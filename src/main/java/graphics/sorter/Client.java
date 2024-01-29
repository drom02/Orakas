package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Structs.ClientMonth;

public class Client extends ClientProfile{


    private ClientMonth clientsMonth;
    @JsonCreator
    public Client(@JsonProperty("name")String name, @JsonProperty("surname")String surname,@JsonProperty("clientMonth")ClientMonth clientMonth){
        super(name, surname);
        setClientsMonth(clientMonth);
    }

    public ClientMonth getClientsMonth() {
        return clientsMonth;
    }

    public void setClientsMonth(ClientMonth clientsMonth) {
        this.clientsMonth = clientsMonth;
    }
    public ClientProfile convertToClientProfile(){
        ClientProfile clP = new ClientProfile(this.getName(), this.getSurname());
        return  clP;
    }
}
