package graphics.sorter.Filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class HardFilters {

    public ArrayList<UUID> removePreviousShift(ArrayList<UUID> input, int day, HashMap<String,ArrayList<ArrayList<UUID>>> hashmap, int daystate){
        ArrayList<UUID> toRemove = new ArrayList<>();
        System.out.println("Entry input " + input);

        if(hashmap.get(String.valueOf(day-1))!=null){
            ArrayList<UUID> listOf = hashmap.get(String.valueOf(day-1)).get(daystate^1);
            listOf.addAll(hashmap.get(String.valueOf(day)).get(daystate^1));
            listOf.addAll(hashmap.get(String.valueOf(day)).get(daystate));
            for(UUID id : input){
                if(listOf.contains(id)){
                    toRemove.add(id);
                }
            }
            input.removeAll(toRemove);
            System.out.println("To remove" + toRemove);
            System.out.println("Final input " +input);
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
    }
}
