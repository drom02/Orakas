package Orakas.Structs;

import Orakas.Humans.Assistant;
import Orakas.JsonManip;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class ListOfAssistants implements Saveable {


    private ArrayList<Assistant> assistantList;
    public ArrayList<Assistant> getAssistantList() {
        return assistantList.stream().filter(f->f.getActivityStatus()==true).collect(Collectors.toCollection(ArrayList::new));
    }
    @JsonIgnore
    public ArrayList<Assistant> getFullAssistantList() {
        return assistantList;
    }

    public void setAssistantList(ArrayList<Assistant> assistantList) {
        this.assistantList = assistantList;
    }
     @JsonCreator
     public ListOfAssistants(@JsonProperty("assistantList") ArrayList<Assistant> assistantList) {
        this.assistantList = assistantList;
    }
    @JsonIgnore
    public Assistant getAssistantFromID(UUID id){
        for(Assistant as : assistantList){
            if(id.equals(as.getID())){
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
