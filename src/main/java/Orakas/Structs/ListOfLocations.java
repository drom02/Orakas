package Orakas.Structs;

import Orakas.JsonManip;
import Orakas.Location;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ListOfLocations implements Saveable {

    public ArrayList<Location> getListOfLocations() {
        return listOfLocations;
    }

    public void setListOfLocations(ArrayList<Location> listOfLocations) {
        this.listOfLocations = listOfLocations;
    }

    public ArrayList<Location>  listOfLocations = new ArrayList<Location>();
    @JsonCreator
    public ListOfLocations(){
        this.listOfLocations = new ArrayList<Location>();
    }
    public Location retrieveById(UUID input){
        return listOfLocations.stream()
                .filter(c -> c.getID().equals(input))
                .findFirst()
                .orElse(null);
    }
    public void createNew(JsonManip map) throws IOException {
        ListOfLocations los = new ListOfLocations();
        map.saveLocations(los);
    }
}
