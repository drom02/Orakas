package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Structs.ClientMonth;
import java.util.UUID;

public class Location {


    private UUID ID;
    private String address;
    private String casualName;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    private String comments;


    @JsonCreator
    public Location(@JsonProperty("ID") UUID ID, @JsonProperty("address") String address, @JsonProperty("casualName") String casualName){
        setID(ID);
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
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Location or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Location)) {
            return false;
        }
        // Compare the data members and return accordingly
        return this.ID.equals(((Location) o).getID());
    }
    @Override
    public int hashCode() {
        return this.ID.hashCode();
    }
    @Override
    public String toString(){
        return getCasualName();
    }
}
