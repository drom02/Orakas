package Orakas.Filters;

import Orakas.Humans.Assistant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import Orakas.Structs.ListOfAssistants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ListOfAssistantMonthShifts {
    public ArrayList<AssistantMonthShift> monthShiftList;
    public ArrayList<AssistantMonthShift> getMonthShiftList() {
        return monthShiftList;
    }
    private HashMap<UUID, Integer> mapOfLatestWork = new HashMap<>();
    private HashMap<UUID, Long> mapOfTotalWork = new HashMap<>();

    public void setMonthShiftList(ArrayList<AssistantMonthShift> monthShiftList) {
        this.monthShiftList = monthShiftList;
    }
    @JsonCreator
    ListOfAssistantMonthShifts(@JsonProperty("monthShiftList") ListOfAssistants assistList) {
        if(assistList==null){
            setMonthShiftList(new ArrayList<AssistantMonthShift>());
        }else{
            setMonthShiftList(new ArrayList<AssistantMonthShift>());
            for(Assistant as:assistList.getAssistantList()){
                getMonthShiftList().add(new AssistantMonthShift(as.getID()));
            }
        }


}
public AssistantMonthShift getMontShift(UUID id ){
        for(AssistantMonthShift mon: monthShiftList){
            if(mon.getAssistantID().equals(id)){
                return mon;

            }
        }
        return null;
}
public HashMap<UUID, Integer> generateHashMapLatestShift(ArrayList<UUID> available){
    for(AssistantMonthShift ms :monthShiftList){
        if(available.contains(ms.getAssistantID())){
            mapOfLatestWork.put(ms.getAssistantID(),ms.getLastDayInWork());
        }

    }
return mapOfLatestWork;
}
public HashMap<UUID, Long> generateHashMapTotalWork(ArrayList<UUID> available){
        for(AssistantMonthShift ms :monthShiftList){
            if(ms.getAssistantID().equals(available)) {
                mapOfTotalWork.put(ms.getAssistantID(), ms.getWorkedHours());
            }
        }
    return  mapOfTotalWork;
    }
}
