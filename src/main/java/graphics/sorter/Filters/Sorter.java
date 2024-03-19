package graphics.sorter.Filters;

import graphics.sorter.Assistant;
import graphics.sorter.Structs.ListOfAssistants;
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


    AssistantMonthWorks workMonth;
    /*
    private ListOfAssistantMonthShifts assistantMonthShifts;
    public Sorter(ListOfAssistants assistList){
        for(int i=0;i<31;i++){
            past.put(String.valueOf(i), new ArrayList<ArrayList<UUID>>(List.of(new ArrayList<UUID>(),new ArrayList<UUID>() )));
        }
       assistantMonthShifts = new ListOfAssistantMonthShifts(assistList);
        public ListOfAssistantMonthShifts getAssistantMonthShifts() {
        return assistantMonthShifts;
    }
    }
     */
    //
    public Sorter(ListOfAssistants assistList){
        workMonth = new AssistantMonthWorks(assistList);
        for(int i=0;i<31;i++){
            past.put(String.valueOf(i), new ArrayList<ArrayList<UUID>>(List.of(new ArrayList<UUID>(),new ArrayList<UUID>() )));
        }
        }
    private HardFilters hardFilters = new HardFilters();
    private SoftFilters softFilters = new SoftFilters();


    public UUID sort(ArrayList<Assistant> availableAssistants,int day, int dayState, ClientDay cl){
        //cl.getClient();
        ArrayList<UUID> availableAssistantsID = getIdFromList(availableAssistants);
        hardFilters.removePreviousShift(availableAssistantsID,day,past,dayState);
        hardFilters.assureProperPause(availableAssistantsID,workMonth,cl.getDayIntervalListUsefull().getFirst().getStart());
        /*
        Soft filters have to be applied after all hard filters.
         */
        //TODO problem is with availableAssistants and availableAssistantsID
        HashMap<UUID,Integer> soft;
        ArrayList<Assistant> trimmedAssistants = new ArrayList<>();
        for(Assistant a : availableAssistants){
            if(availableAssistantsID.contains(a.getID())){
                trimmedAssistants.add(a);
            }
        }
        if(!availableAssistantsID.isEmpty()) {
            soft = softFilters.prepare(availableAssistantsID);
            softFilters.penalizeRecent(soft,workMonth.getLastWorkedDay(),day,1);
            softFilters.clientPreference(soft,cl.getClient(),trimmedAssistants);
            softFilters.emergencyAssistant(soft,trimmedAssistants);
            softFilters.output(availableAssistantsID,soft);
        }


        ArrayList<ArrayList<UUID>> tempList = past.get(String.valueOf(day));
        if(!availableAssistantsID.isEmpty()){
           // System.out.println(availableAssistantsID.get(0));
            tempList.get(dayState).add(availableAssistantsID.get(0));
            past.put(String.valueOf(day),tempList);
            Assistant pickedForDay = trimmedAssistants.stream()
                    .filter(c -> c.getID().equals(availableAssistantsID.get(0)))
                    .findFirst()
                    .orElse(null);
            long lenghtOfShift = 0;
            for (ServiceInterval sevInt : cl.getDayIntervalListUsefull()) {
                sevInt.setOverseeingAssistant(pickedForDay);
                lenghtOfShift = lenghtOfShift + sevInt.getIntervalLength();
            }
            AssistantWorkShift workShift= new AssistantWorkShift(availableAssistantsID.get(0),cl);
            workMonth.registerWorkDay(workShift);
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
    public void report(){
        for(UUID id: workMonth.getFinishedWork().keySet()){
            System.out.println(id);
            for(AssistantWorkShift list :workMonth.getFinishedWork().get(id)){
                System.out.println(list.getDay());
                System.out.println(list.getWorkedHours());
            }
        }
    }
}
