package graphics.sorter.Filters;

import graphics.sorter.Assistant;
import graphics.sorter.Database;
import graphics.sorter.Structs.ListOfAssistants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AssistantMonthWorks {
    public HashMap<UUID, HashMap<Integer, AssistantWorkShift>> getFinishedWork() {
        return finishedWork;
    }

    //den = day int
    // noc = day int + 100
    public void setFinishedWork(HashMap<UUID, HashMap<Integer, AssistantWorkShift>> finishedWork) {
        this.finishedWork = finishedWork;
    }
    private HashMap<UUID,HashMap<Integer, AssistantWorkShift>> finishedWork = new HashMap<UUID,HashMap<Integer, AssistantWorkShift>>();
    private HashMap<UUID, Integer> lastWorkedDay = new HashMap<UUID, Integer>();
    private HashMap<UUID, LocalDateTime> lastWorkedDayTime = new HashMap<UUID, LocalDateTime>();

    public HashMap<UUID, LocalDateTime> getLastWorkedDayTime() {
        return lastWorkedDayTime;
    }

    public void setLastWorkedDayTime(HashMap<UUID, LocalDateTime> lastWorkedDayTime) {
        this.lastWorkedDayTime = lastWorkedDayTime;
    }


    public AssistantMonthWorks(ListOfAssistants listOfAssistants){
        for(Assistant a : listOfAssistants.getFullAssistantList()){
            finishedWork.put(a.getID(),new HashMap<Integer, AssistantWorkShift>());
            lastWorkedDay.put(a.getID(),0);
        }
    }
    public HashMap<UUID, Integer> getLastWorkedDay(){
        return lastWorkedDay;
    }
    private void addWork(AssistantWorkShift work){
        finishedWork.get(work.getAssistantID()).put(work.getDay(),work);
    }
    public int getLastDayInWork(UUID id ) {
        return lastWorkedDay.get(id);
    }

    public void setLastDayInWork(UUID id,int lastDayInWork) {
        lastWorkedDay.put(id,lastDayInWork);
    }
    public void registerWorkDay(AssistantWorkShift work){
        finishedWork.get(work.getAssistantID()).put((work.isType()== true)?work.getDay() :(work.getDay()+100) ,work);
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
