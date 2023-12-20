package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Structs.ClientMonth;

public class Client extends Human {


    private ClientMonth clientsMonth;
    @JsonCreator
    public Client(@JsonProperty("name")String name, @JsonProperty("surname")String surname,@JsonProperty("clientMonth")ClientMonth clientMonth){
        setClientsMonth(clientMonth);
        setName(name);
        setSurname(surname);
    }
    public ClientMonth getClientsMonth() {
        return clientsMonth;
    }

    public void setClientsMonth(ClientMonth clientsMonth) {
        this.clientsMonth = clientsMonth;
    }
}
