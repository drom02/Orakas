package graphics.sorter.AssistantAvailability;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.UUID;

public class ShiftAvailabilityArray {


    ArrayList<ShiftAvailability> main;
@JsonCreator
    public ShiftAvailabilityArray(@JsonProperty("main") ArrayList<ShiftAvailability> main){
        setMain(main);

    }
    public ArrayList<ShiftAvailability> getMain() {
        return main;
    }

    public void setMain(ArrayList<ShiftAvailability> main) {
        this.main = main;
    }
}
