package graphics.sorter.Filters;

import java.util.*;
import java.util.stream.Collectors;

public class SoftFilters {


    public void penalizeRecent(ArrayList<UUID> availableAssistants, HashMap<UUID, Integer> mapOfLatestWork, int day, int penaly){
         LinkedHashMap<UUID,Integer> linkedMapOfLatestWork= new LinkedHashMap<>();
        System.out.println("Start " +availableAssistants);
        for(UUID id : availableAssistants){
            linkedMapOfLatestWork.put(id,(penaly*Math.abs(mapOfLatestWork.get(id)-day)));
            System.out.println(id + " "+(penaly*Math.abs(mapOfLatestWork.get(id)-day)));

        }
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

    }
}
