package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import graphics.sorter.Location;

import java.util.ArrayList;

public class ListOfLocations {

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
}
