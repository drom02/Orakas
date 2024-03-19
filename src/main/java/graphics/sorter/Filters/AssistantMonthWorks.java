package graphics.sorter.Filters;

import graphics.sorter.Assistant;
import graphics.sorter.Database;
import graphics.sorter.Structs.ListOfAssistants;

import java.time.LocalDateTime;
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

    public HashMap<UUID, LocalDateTime> getLastWorkedDayTime() {
        return lastWorkedDayTime;
    }

    public void setLastWorkedDayTime(HashMap<UUID, LocalDateTime> lastWorkedDayTime) {
        this.lastWorkedDayTime = lastWorkedDayTime;
    }

    private HashMap<UUID, LocalDateTime> lastWorkedDayTime = new HashMap<UUID, LocalDateTime>();
    public AssistantMonthWorks(ListOfAssistants listOfAssistants){
        for(Assistant a : listOfAssistants.getFullAssistantList()){
            finishedWork.put(a.getID(),new ArrayList<AssistantWorkShift>());
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
        if(lastWorkedDayTime.get(work.getAssistantID()) != null){
            if(lastWorkedDayTime.get(work.getAssistantID()).isBefore(work.getEnd())){
                lastWorkedDayTime.put(work.getAssistantID(),work.getEnd());
            }
        }else{
            lastWorkedDayTime.put(work.getAssistantID(),work.getEnd());
        }

    }

}
