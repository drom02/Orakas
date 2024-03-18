package graphics.sorter.Filters;

import graphics.sorter.Assistant;
import graphics.sorter.Database;
import graphics.sorter.Structs.ListOfAssistants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AssistantMonthWorks {
    public HashMap<UUID, ArrayList<AssistantWorkShift>> getFinishedWork() {
        return finishedWork;
    }

    public void setFinishedWork(HashMap<UUID, ArrayList<AssistantWorkShift>> finishedWork) {
        this.finishedWork = finishedWork;
    }

    private HashMap<UUID,ArrayList<AssistantWorkShift>> finishedWork = new HashMap<UUID,ArrayList<AssistantWorkShift>>();
    private HashMap<UUID, Integer> lastWorkedDay = new HashMap<UUID, Integer>();
    public AssistantMonthWorks(ListOfAssistants listOfAssistants){
        for(Assistant a : listOfAssistants.getFullAssistantList()){
            finishedWork.put(a.getID(),new ArrayList<AssistantWorkShift>());
        }
        for(Assistant a : listOfAssistants.getFullAssistantList()){
            lastWorkedDay.put(a.getID(),0);
        }
    }
    public HashMap<UUID, Integer> getLastWorkedDay(){
        return lastWorkedDay;
    }
    private void addWork(AssistantWorkShift work){
        finishedWork.get(work.getAssistantID()).add(work);
    }
    public int getLastDayInWork(UUID id ) {
        return lastWorkedDay.get(id);
    }

    public void setLastDayInWork(UUID id,int lastDayInWork) {
        lastWorkedDay.put(id,lastDayInWork);
    }
    public void registerWorkDay(AssistantWorkShift work){
        finishedWork.get(work.getAssistantID()).add(work);
        lastWorkedDay.put(work.getAssistantID(),work.getDay());
    }

}
