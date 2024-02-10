package graphics.sorter.Filters;

import graphics.sorter.Assistant;
import graphics.sorter.ListOfAssistants;
import graphics.sorter.Structs.ClientDay;
import graphics.sorter.Structs.ServiceInterval;

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

    public ListOfAssistantMonthShifts getAssistantMonthShifts() {
        return assistantMonthShifts;
    }

    private ListOfAssistantMonthShifts assistantMonthShifts;
    private HardFilters hardFilters = new HardFilters();
    private SoftFilters softFilters = new SoftFilters();
    public Sorter(ListOfAssistants assistList){
        for(int i=0;i<31;i++){
            past.put(String.valueOf(i), new ArrayList<ArrayList<UUID>>(List.of(new ArrayList<UUID>(),new ArrayList<UUID>() )));
        }
       assistantMonthShifts = new ListOfAssistantMonthShifts(assistList);
    }

    public UUID sort(ArrayList<Assistant> availableAssistants,int day, int dayState, ClientDay cl){
        ArrayList<UUID> availableAssistantsID = getIdFromList(availableAssistants);
        hardFilters.removePreviousShift(availableAssistantsID,day,past,dayState);
        /*
        Soft filters have to be applied after all hard filters.
         */
         softFilters.penalizeRecent(availableAssistantsID,getAssistantMonthShifts().generateHashMapLatestShift(availableAssistantsID),day,1);

        ArrayList<ArrayList<UUID>> tempList = past.get(String.valueOf(day));
        if(!availableAssistantsID.isEmpty()){
            System.out.println(availableAssistantsID.get(0));
            tempList.get(dayState).add(availableAssistantsID.get(0));
            past.put(String.valueOf(day),tempList);
            Assistant pickedForDay = availableAssistants.stream()
                    .filter(c -> c.getID().equals(availableAssistantsID.get(0)))
                    .findFirst()
                    .orElse(null);
            long lenghtOfShift = 0;
            for (ServiceInterval sevInt : cl.getDayIntervalListUsefull()) {
                sevInt.setOverseeingAssistant(pickedForDay);
                lenghtOfShift = lenghtOfShift + sevInt.getIntervalLength();
            }
            AssistantMonthShift editedShift = assistantMonthShifts.getMontShift(availableAssistantsID.get(0));
            editedShift.setLastDayInWork(day);
            editedShift.setWorkedHours(editedShift.getWorkedHours()+ lenghtOfShift);
            return availableAssistantsID.get(0);
        }else{
            for (ServiceInterval sevInt : cl.getDayIntervalListUsefull()) {
                sevInt.setOverseeingAssistant(null);
            }
            return  null;
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
