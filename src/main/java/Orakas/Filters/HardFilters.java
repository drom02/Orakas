package Orakas.Filters;

import Orakas.Humans.Assistant;
import Orakas.Structs.TimeStructs.ClientDay;
import Orakas.Structs.ListOfAssistants;
import Orakas.Structs.TimeStructs.ServiceIntervalArrayList;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

public class HardFilters {

    public ArrayList<UUID> removePreviousShift(ArrayList<UUID> input, int day, AssistantMonthWorks aMW, int daystate){
        ArrayList<UUID> toRemove = new ArrayList<>();
        day = day+1;
        if(day == 2){
            System.out.println();
        }
        for(UUID id : input){
            if(aMW.getFinishedWork().get(id).get(day) != null){
                toRemove.add(id);
            }
            if(aMW.getFinishedWork().get(id).get(day+100) != null){
                toRemove.add(id);
            }
        }
        input.removeAll(toRemove);
        return input;

        /*
        if(day == 0){
            return input;
        }
        //day=day-1;
        //report(day,input, "input");
        ArrayList<UUID> toRemove = new ArrayList<>();
       // System.out.println("Entry input " + input);
        if(hashmap.get(String.valueOf(day))!=null){
            ArrayList<UUID> listOf = hashmap.get(String.valueOf(day)).get(daystate^1);
            listOf.addAll(hashmap.get(String.valueOf(day)).get(daystate^1));
            listOf.addAll(hashmap.get(String.valueOf(day)).get(daystate));
            for(UUID id : input){
                if(listOf.contains(id)){
                    toRemove.add(id);
                }
            }
            input.removeAll(toRemove);
            //System.out.println("To remove" + toRemove);
           // System.out.println("Final input " +input);
          //  report(day,input,"output");
            return input;
        }else{
            ArrayList<UUID> listOf = hashmap.get(String.valueOf(day)).get(daystate);
            listOf.addAll(hashmap.get(String.valueOf(day)).get(daystate^1));
            for(UUID id : input){
                if(listOf.contains(id)){
                    toRemove.add(id);
                }
            }
            input.removeAll(toRemove);
            return input;
        }
         */

    }
    private String toStringPrint(ArrayList input){
        StringBuilder st = new StringBuilder();
        for(int i =0; i< input.size();i++){
            st.append(input.get(i).toString() );
            st.append(",");
        }
        return st.toString();
    }
    public ArrayList<UUID> limitWorkedTime(ArrayList<UUID> input, ListOfAssistantMonthShifts assistantMonthShifts){
        ArrayList<UUID> toRemove = new ArrayList<>();
        for(UUID id : input){
            if(assistantMonthShifts.getMontShift(id).getWorkedHours()>0)
            {toRemove.add(id);
            }
        }
        input.removeAll(toRemove);
        return  input;
    }
    public ArrayList<UUID>  assureProperPause(ArrayList<UUID> input, AssistantMonthWorks workMonth, ServiceIntervalArrayList currentUsefull){
        if(!currentUsefull.isEmpty()){
            LocalDateTime startOfCurrent = currentUsefull.getFirst().getStart();
            ArrayList<UUID> toRemove = new ArrayList<>();
            for(UUID id : input){
                LocalDateTime last = workMonth.getLastWorkedDayTime().get(id);
                if( last != null && ChronoUnit.MINUTES.between(last,startOfCurrent) < (11*60)  ){
                    toRemove.add(id);
                }
            }
            input.removeAll(toRemove);
        }
        return input;
    }
    //TODO change the 12 to use length of current work day
    public ArrayList<UUID>  removeByWorkTime(ArrayList<UUID> input, int day, ListOfAssistants asL, AssistantMonthWorks wok){
        ArrayList<UUID> toRemove = new ArrayList<>();
        for(UUID id : input){
           Assistant aTemp = asL.getAssistantFromID(id);
           if(wok.getWorkedTillDate(day,id) > aTemp.getContractTime()-12 && !aTemp.getContractType().equals("HPP")&& !aTemp.getContractType().equals("HPP-Vlastní")){
                toRemove.add(id);
           }
        }
        input.removeAll(toRemove);

        return input;
    }
    public ArrayList<UUID>  removeByCompatibility(ArrayList<UUID> input, ListOfAssistants asL, ClientDay cl){
        ArrayList<UUID> toRemove = new ArrayList<>();
        for(UUID id : input){
            Assistant aTemp = asL.getAssistantFromID(id);
            if(aTemp.getClientPreference().getLast().contains(cl.getClient())){
                toRemove.add(id);
            }
        }
        input.removeAll(toRemove);

        return input;
    }
    public ArrayList<UUID>  removeByTooFrequent(ArrayList<UUID> input, AssistantMonthWorks workMonth, ClientDay cl){
        ArrayList<UUID> toRemove = new ArrayList<>();
        int switchValue = 0;
        if(!cl.getDayStatus()){
            switchValue = 100;
        }
        for(UUID id : input){
            if(workMonth.getFinishedWork().get(id)!=null){
                if(doubleCheck(workMonth,id,cl,1) && doubleCheck(workMonth,id,cl,2)&& doubleCheck(workMonth,id,cl,3)){
                    toRemove.add(id);
                }
            }
        }
        input.removeAll(toRemove);

        return input;
    }
    private boolean doubleCheck(AssistantMonthWorks workMonth, UUID id, ClientDay cl, int shift){
        if(workMonth.getFinishedWork().get(id).get(cl.getDay()-shift)!=null || workMonth.getFinishedWork().get(id).get(cl.getDay()+100-shift)!=null  ){
            return true;
        }
        return false;
    }
}
