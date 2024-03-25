package graphics.sorter.Filters;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
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
    public ArrayList<UUID>  assureProperPause(ArrayList<UUID> input, AssistantMonthWorks workMonth, LocalDateTime startOfCurrent){
        ArrayList<UUID> toRemove = new ArrayList<>();
        for(UUID id : input){
            LocalDateTime last = workMonth.getLastWorkedDayTime().get(id);
                if( last != null && ChronoUnit.MINUTES.between(last,startOfCurrent) < (8*60)  ){
                    toRemove.add(id);
                }
        }
        input.removeAll(toRemove);
        return input;
    }
}
