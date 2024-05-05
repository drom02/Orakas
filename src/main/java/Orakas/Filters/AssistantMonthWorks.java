package Orakas.Filters;


import Orakas.Humans.Assistant;
import Orakas.Structs.ListOfAssistants;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
/*
Class used for tracking work done by assistant for purposes of filtering  when selecting the best assistant for shift.
 */
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
    public long getWorkedTillDate(int day, UUID id ){
        HashMap<Integer, AssistantWorkShift>  map= getFinishedWork().get(id);
        long output = 0;
        for(int i = 1; i <= day;i++){
            output += (map.get(i) != null) ? map.get(i).getWorkedMinutes() : 0;
            output += (map.get(i+100) != null) ? map.get(i+100).getWorkedMinutes() : 0;
        }
        return output;
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
