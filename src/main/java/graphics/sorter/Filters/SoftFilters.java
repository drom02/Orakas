package graphics.sorter.Filters;

import graphics.sorter.Assistant;

import java.util.*;
import java.util.stream.Collectors;

public class SoftFilters {

    public HashMap<UUID,Integer> prepare(ArrayList<UUID> availableAssistants){
     HashMap<UUID,Integer> ratingMap = new HashMap<>();
     for(UUID id : availableAssistants){
         ratingMap.put(id,0);
     }
     return  ratingMap;
    }
    public void output(ArrayList<UUID> availableAssistants,HashMap<UUID,Integer> ratingMap){
        LinkedHashMap linkedMapOfLatestWork = ratingMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // Merge function, in case of duplicate keys (should not happen in this context)
                        LinkedHashMap::new));
        availableAssistants.clear();
        availableAssistants.addAll(linkedMapOfLatestWork.keySet());
    }
    public void penalizeRecent(HashMap<UUID,Integer> input, HashMap<UUID, Integer> mapOfLatestWork, int day, int penaly){
        for(UUID id : input.keySet()){
            input.put(id,(penaly*Math.abs(mapOfLatestWork.get(id)-day)));
            //linkedMapOfLatestWork.put(id,(penaly*Math.abs(mapOfLatestWork.get(id)-day)));
          //  System.out.println(id + " "+(penaly*Math.abs(mapOfLatestWork.get(id)-day)));

        }
        /*
        linkedMapOfLatestWork = linkedMapOfLatestWork.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // Merge function, in case of duplicate keys (should not happen in this context)
                        LinkedHashMap::new));
        availableAssistants.clear();
        availableAssistants.addAll(linkedMapOfLatestWork.keySet());
        System.out.println("End " +availableAssistants);
         */


    }
    public void clientPreference(HashMap<UUID,Integer> ratingMap, UUID client, ArrayList<Assistant> listOfAssistants){
        for(UUID id : ratingMap.keySet()){
            Assistant as = getfromID(listOfAssistants,id);
            ratingMap.put(id, (ratingMap.get(id)*getPenalty(as,client)));
           // System.out.println(getPenalty(as,client));
        }

    }
    private Assistant getfromID(ArrayList<Assistant> inp, UUID id){
        for(Assistant as : inp){
            if(id == as.getID()){
                return as;
            }
        }
        return  null;
    }
    private int getPenalty(Assistant as, UUID client){
        switch (getCategory(as,client)){
            case 0:
                return 2;
            case 1:
                return 4;
            case 2:
                return 1;
        }
        return 1;
    }
    private int getCategory(Assistant as, UUID client){
        int i = 0;
        System.out.println(as.getName() +" "+ as.getSurname());
        for(ArrayList<UUID> idList :as.getClientPreference()){
            System.out.println(client);
            if(idList.contains(client)){
                System.out.println("Check");
                System.out.println(i);
                return i;
            }
            i++;
        }
        return 0;
    }
}
