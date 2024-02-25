package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.Structs.Saveable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ListOfAssistants implements Saveable {


    public ArrayList<Assistant> assistantList;
    public ArrayList<Assistant> getAssistantList() {
        return assistantList;
    }

    public void setAssistantList(ArrayList<Assistant> assistantList) {
        this.assistantList = assistantList;
    }
     @JsonCreator
     ListOfAssistants(@JsonProperty("assistantList") ArrayList<Assistant> assistantList) {
        this.assistantList = assistantList;
    }
    @JsonIgnore
    public Assistant getAssistantFromID(UUID id){
        for(Assistant as : assistantList){
            if(id == as.getID()){
               return as;
                }
            }
        return  null;
    }
    @Override
    public void createNew(JsonManip map) throws IOException {
        ListOfAssistants los = new ListOfAssistants(new ArrayList<Assistant>());
        map.saveAssistantInfo(los);
    }
}
