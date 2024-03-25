package graphics.sorter.Filters;

import graphics.sorter.Assistant;
import graphics.sorter.Structs.ListOfAssistants;

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
    public void penalizeRecent(HashMap<UUID,Integer> input, HashMap<UUID, Integer> mapOfLatestWork, int day, int penalty){
            for(UUID id : input.keySet()) {
                if (mapOfLatestWork.get(id) != null) {
                    input.put(id, (penalty * Math.abs(mapOfLatestWork.get(id) - day)));
                }
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
    public void workedHoursHPP(HashMap<UUID,Integer> ratingMap, int day, AssistantMonthWorks workMonth, HashMap<UUID, Double> workHoursOfMonth, ListOfAssistants listOfAssistants){
        for(UUID id : ratingMap.keySet()){
            if(listOfAssistants.getAssistantFromID(id).getContractType().equals("HPP")){
                ratingMap.put(id, (int) (ratingMap.get(id)+((workHoursOfMonth.get(id)/60)- (workMonth.getWorkedTillDate(day,id)/60))));
            }

        }
    }
    public void clientPreference(HashMap<UUID,Integer> ratingMap, UUID client, ArrayList<Assistant> listOfAssistants){
        for(UUID id : ratingMap.keySet()){
            Assistant as = getfromID(listOfAssistants,id);
            ratingMap.put(id, (ratingMap.get(id)*getPenalty(as,client)));
           // System.out.println(getPenalty(as,client));
        }

    }
    public void emergencyAssistant(HashMap<UUID,Integer> ratingMap,ArrayList<Assistant> listOfAssistants){
        for(Assistant a : listOfAssistants){
            if(a.isEmergencyAssistant()){
                ratingMap.put(a.getID(), -999999999);
            }
        }

    }
    private Assistant getfromID(ArrayList<Assistant> inp, UUID id){
        for(Assistant as : inp){
            if(id.equals(as.getID())){
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
      //  System.out.println(as.getName() +" "+ as.getSurname());
        for(ArrayList<UUID> idList :as.getClientPreference()){
         //   System.out.println(client);
            if(idList.contains(client)){
             //   System.out.println("Check");
            //    System.out.println(i);
                return i;
            }
            i++;
        }
        return 0;
    }

}
