package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ListOfAssistants {


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
}
