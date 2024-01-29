package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Structs.ClientMonth;

public class ClientProfile extends Human {



    private String ClientId;

    @JsonCreator
    public ClientProfile(@JsonProperty("name")String name, @JsonProperty("surname")String surname){
        setClientId(surname);
        setName(name);
        setSurname(surname);
    }
    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public Client convertToClient(ClientMonth assignedMonth){
        Client output = new Client(getName(), getSurname(),assignedMonth);
        return output;
    }

}
