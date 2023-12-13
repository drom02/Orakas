package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Client extends Human {
    @JsonCreator
    public Client(@JsonProperty("name")String name, @JsonProperty("surname")String surname){
        setName(name);
        setSurname(surname);


    }

}
