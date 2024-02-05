package graphics.sorter.Filters;

import graphics.sorter.Assistant;
import graphics.sorter.ListOfAssistants;
import graphics.sorter.Structs.ClientMonth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;



public class Sorter {
    //private ArrayList<ArrayList<UUID>> past = new ArrayList<ArrayList<UUID>>();
    private HashMap<String, ArrayList<ArrayList<UUID>>> past = new HashMap<>();
    /*
    Used for record keeping.
     */
    private int day;
    private ArrayList<AssistantMonthShift> assistantMonthShifts = new ArrayList<AssistantMonthShift>();
    private HardFilters hardFilters = new HardFilters();
    public Sorter(ListOfAssistants assistList){
        for(int i=0;i<31;i++){
            past.put(String.valueOf(i), new ArrayList<ArrayList<UUID>>(List.of(new ArrayList<UUID>(),new ArrayList<UUID>() )));
        }
        for(Assistant as:assistList.getAssistantList()){
            assistantMonthShifts.add(new AssistantMonthShift(as.getID()));
        }
    }

    public UUID sort(ArrayList<UUID> availableAssistants,int day, int dayState){
       // System.out.println(day);
       // System.out.println(dayState);
        hardFilters.removePreviousShift(availableAssistants,day,past,dayState);
        ArrayList<ArrayList<UUID>> tempList = past.get(String.valueOf(day));
        if(!availableAssistants.isEmpty()){
            tempList.get(dayState).add(availableAssistants.get(0));
            past.put(String.valueOf(day),tempList);

           // System.out.println(past.get(String.valueOf(day)));
           // System.out.println(availableAssistants);
            //System.out.println(availableAssistants.get(0));
            return availableAssistants.get(0);
        }else{
            return null;
        }

    }
    public ArrayList<UUID> getIdFromList(ArrayList<Assistant> input){
        ArrayList<UUID> output = new ArrayList<>();
        for (Assistant as : input){
            output.add(as.getID());

        }
        return  output;
    }
}
