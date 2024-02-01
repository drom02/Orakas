package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Structs.ClientMonth;
import java.util.UUID;

public class Location {


    private UUID ID;
    private String address;
    private String casualName;


    @JsonCreator
    Location(@JsonProperty("ID")UUID dummyVal, @JsonProperty("address")String address, @JsonProperty("casualName") String casualName){
        setID(UUID.randomUUID());
        setAddress(address);
        setCasualName(casualName);
    }
    public UUID getID() {
        return ID;
    }

    public void setID(UUID ID) {
        this.ID = ID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCasualName() {
        return casualName;
    }

    public void setCasualName(String casualName) {
        this.casualName = casualName;
    }

}
