package Orakas.Filters;

import Orakas.Humans.Assistant;
import Orakas.AssistantAvailability.AssistantAvailability;
import Orakas.Database.Database;
import Orakas.MergedIntervals.MergedRegistry;
import Orakas.Structs.TimeStructs.ClientDay;
import Orakas.Structs.TimeStructs.ServiceInterval;
import Orakas.workHoursAllocation.WorkHoursCalcul;
import Orakas.AssistantAvailability.DateTimeAssistantAvailability;
import Orakas.Structs.ListOfAssistants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;



public class Sorter {
    //private ArrayList<ArrayList<UUID>> past = new ArrayList<ArrayList<UUID>>();
    private HashMap<String, ArrayList<ArrayList<UUID>>> past = new HashMap<>();
    /*
    Used for record keeping.
     */
    private int day;
    private IntervalProcessing interProc ;
    private HashMap<UUID, DateTimeAssistantAvailability> IDAvailIndex;
    private HashMap<UUID, Assistant> assistantHashMap = new HashMap<>();
    private HashMap<UUID, Double> workHoursOfMonth = new HashMap<>();
    private AssistantMonthWorks workMonth;
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
    private void setupWorkHoursOfMonth(ListOfAssistants assistList, int year, int month ){
        LocalDate tempDate = LocalDate.of(year,month,1).plusMonths(-1);
        WorkOfMonth wok = Database.loadMonthWorkResult(tempDate.getYear(),tempDate.getMonthValue());
        for(Assistant a : assistList.getFullAssistantList()){
            if(a.getContractType().equals("HPP")){
                double addedValue = 0;
                if(wok != null && null != wok.retrieveValue(a.getID())){
                    addedValue = wok.retrieveValue(a.getID());
                }
                workHoursOfMonth.put(a.getID(), WorkHoursCalcul.workDaysCalcul(year,month,7.5,a.getID(),a.getContractTime())+addedValue);
            } else if (a.getContractType().equals("HPP-Vlastní")) {
                workHoursOfMonth.put(a.getID(), a.getContractTime());
            }
        }
    }

    public AssistantMonthWorks getWorkMonth(){
        return workMonth;
    }
    public HashMap<UUID, Double> getWorkHoursOfMonth(){
        return workHoursOfMonth;
    }
    public Sorter(ListOfAssistants assistList, int year, int month, MergedRegistry mergedRegistry){
        IDAvailIndex = new HashMap<UUID, DateTimeAssistantAvailability>();
        workMonth = new AssistantMonthWorks(assistList);
        setupWorkHoursOfMonth(assistList,year,month);
        prepareIndex(assistList);
        interProc = new IntervalProcessing(workMonth, Database.loadSettings().getMaxShiftLength(),assistantHashMap,mergedRegistry);
        }
    private HardFilters hardFilters = new HardFilters();
    private SoftFilters softFilters = new SoftFilters();


    public UUID sort(ArrayList<AssistantAvailability> availableAssistants, int day, int dayState, ClientDay cl, ListOfAssistants aslList){
        /*

         */
        //cl.getClient();
        ArrayList<UUID> availableAssistantsID = getIdFromList(availableAssistants);
        hardFilters.removePreviousShift(availableAssistantsID,day,workMonth,dayState);
        hardFilters.assureProperPause(availableAssistantsID,workMonth,cl.getDayIntervalListUsefull().getFirst().getStart());

        /*
        Soft filters have to be applied after all hard filters.
         */
        //TODO problem is with availableAssistants and availableAssistantsID
        HashMap<UUID,Integer> soft;
        ArrayList<Assistant> trimmedAssistants = new ArrayList<>();
        for(Assistant a : aslList.getFullAssistantList()){
            if(availableAssistantsID.contains(a.getID())){
                trimmedAssistants.add(a);
            }
        }
        if(!availableAssistantsID.isEmpty()) {
            soft = softFilters.prepare(availableAssistantsID);
            softFilters.penalizeRecent(soft,workMonth.getLastWorkedDay(),day,1);
            softFilters.clientPreference(soft,cl.getClient(),trimmedAssistants);
            softFilters.emergencyAssistant(soft,trimmedAssistants,5);
            softFilters.output(availableAssistantsID,soft);
        }
        ArrayList<ArrayList<UUID>> tempList = past.get(String.valueOf(day));
        if(!availableAssistantsID.isEmpty()){
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
    public UUID sortDate(ArrayList<DateTimeAssistantAvailability> availableAssistants, int day, int dayState, ClientDay cl, ListOfAssistants aslList){
        ArrayList<UUID> availableAssistantsID = getIdFromDateList(availableAssistants,IDAvailIndex);
        hardFilters.removePreviousShift(availableAssistantsID,day,workMonth,dayState);
        hardFilters.assureProperPause(availableAssistantsID,workMonth,cl.getDayIntervalListUsefull().getFirst().getStart());
        hardFilters.removeByWorkTime(availableAssistantsID,day,aslList,workMonth);
        hardFilters.removeByCompatibility(availableAssistantsID,aslList,cl);
        /*
        Soft filters have to be applied after all hard filters.
         */
        HashMap<UUID,Integer> soft;
        ArrayList<Assistant> trimmedAssistants = new ArrayList<>();
        for(Assistant a : aslList.getFullAssistantList()){
            if(availableAssistantsID.contains(a.getID())){
                trimmedAssistants.add(a);
            }
        }
        if(!availableAssistantsID.isEmpty()){
                soft = softFilters.prepare(availableAssistantsID);
                softFilters.penalizeRecent(soft,workMonth.getLastWorkedDay(),day,1);
                softFilters.clientPreference(soft,cl.getClient(),trimmedAssistants);
                softFilters.emergencyAssistant(soft,trimmedAssistants,Scores.getScores().getScoresMap().get("emergency"));
                softFilters.workedHoursHPP(soft,day,workMonth,workHoursOfMonth,aslList,Scores.getScores().getScoresMap().get("HppHour"));
                softFilters.output(availableAssistantsID,soft);
                ArrayList<DateTimeAssistantAvailability>  ordered = orderedDateTimeAA(availableAssistantsID,availableAssistants);
                interProc.start(ordered,cl,dayState);
                return availableAssistantsID.getFirst();
        }else{
            return  null;
        }
    }

    private ArrayList<DateTimeAssistantAvailability> orderedDateTimeAA (ArrayList<UUID> availableAssistantsID,ArrayList<DateTimeAssistantAvailability> availableAssistants ){
        ArrayList<DateTimeAssistantAvailability> temp = new ArrayList<>();
        for(UUID id : availableAssistantsID){
            temp.add(getDateTimeAAFromID(id,availableAssistants));
        }
        return  temp;
    }
    private DateTimeAssistantAvailability getDateTimeAAFromID(UUID id,ArrayList<DateTimeAssistantAvailability> availableAssistants){
        for(DateTimeAssistantAvailability dt :availableAssistants){
            UUID tempAssistant = dt.getAssistantAvailability().getAssistant();
            if(tempAssistant.equals(id)){
                return dt;
            }
        }
        return null;
    }
    public ArrayList<UUID> getIdFromList(ArrayList<AssistantAvailability> input){
        ArrayList<UUID> output = new ArrayList<>();
        for (AssistantAvailability as : input){
            output.add(as.getAssistant());
        }
        return  output;
    }
    public ArrayList<UUID> getIdFromDateList(ArrayList<DateTimeAssistantAvailability> input, HashMap<UUID, DateTimeAssistantAvailability> IDAvailIndex){
        ArrayList<UUID> output = new ArrayList<>();
        for (DateTimeAssistantAvailability as : input){
            UUID id = as.getAssistantAvailability().getAssistant();
            output.add(id);
            IDAvailIndex.put(id,as);
        }
        return  output;
    }
    private void prepareIndex(ListOfAssistants assistants){
        for(Assistant a : assistants.getFullAssistantList()){
            assistantHashMap.put(a.getID(),a);
        }
    }
    public void report(){
        //TODO repair
        /*
        for(UUID id: workMonth.getFinishedWork().keySet()){
            System.out.println(id);
            for(AssistantWorkShift list :workMonth.getFinishedWork().get(id)){
                System.out.println(list.getDay());
                System.out.println(list.getWorkedHours());
            }
        }
         */

    }
}
