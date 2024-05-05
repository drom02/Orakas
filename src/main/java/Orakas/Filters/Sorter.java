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

/*
Class responsible for finding the best available assistant.
 */

public class Sorter {
    private HashMap<String, ArrayList<ArrayList<UUID>>> past = new HashMap<>();
    private int day;
    private IntervalProcessing interProc ;
    private HashMap<UUID, DateTimeAssistantAvailability> IDAvailIndex;
    private HashMap<UUID, Assistant> assistantHashMap = new HashMap<>();
    private HashMap<UUID, Double> workHoursOfMonth = new HashMap<>();
    private AssistantMonthWorks workMonth;
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
    public UUID sortDate(ArrayList<DateTimeAssistantAvailability> availableAssistants, int day, int dayState, ClientDay cl, ListOfAssistants aslList){
        ArrayList<UUID> availableAssistantsID = getIdFromDateList(availableAssistants,IDAvailIndex);
        hardFilters.removePreviousShift(availableAssistantsID,day,workMonth,dayState);
        hardFilters.assureProperPause(availableAssistantsID,workMonth,cl.getDayIntervalListUsefull());
        hardFilters.removeByWorkTime(availableAssistantsID,day,aslList,workMonth);
        hardFilters.removeByCompatibility(availableAssistantsID,aslList,cl);
        hardFilters.removeByTooFrequent(availableAssistantsID,workMonth,cl);
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
                /*
                Start of the process of finding the best assistant.
                 */
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
}
