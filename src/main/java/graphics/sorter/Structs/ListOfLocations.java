package graphics.sorter.Structs;

import com.fasterxml.jackson.annotation.JsonCreator;
import graphics.sorter.Assistant;
import graphics.sorter.JsonManip;
import graphics.sorter.ListOfAssistants;
import graphics.sorter.Location;

import java.io.IOException;
import java.util.ArrayList;

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
    public void createNew(JsonManip map) throws IOException {
        ListOfLocations los = new ListOfLocations();
        map.saveLocations(los);
    }
}
