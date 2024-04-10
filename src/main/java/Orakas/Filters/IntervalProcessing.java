package Orakas.Filters;

import Orakas.Humans.Assistant;
import Orakas.Mediator.ReferentialBoolean;
import Orakas.MergedIntervals.MergedRecord;
import Orakas.MergedIntervals.MergedRegistry;
import Orakas.Settings;
import Orakas.Structs.TimeStructs.ClientDay;
import Orakas.Structs.TimeStructs.ServiceInterval;
import Orakas.Structs.Utility.TimeComparator;
import Orakas.AssistantAvailability.DateTimeAssistantAvailability;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class IntervalProcessing {

    private AssistantMonthWorks workMonth;
    private Integer maxShiftLength;
    private HashMap<UUID, Assistant> assistantHashMap;
    private boolean secondRun = false;
    private HashMap< UUID,Integer> orderHashMap = new HashMap<>();

    public MergedRegistry getMergedRegistry() {
        return mergedRegistry;
    }

    public void setMergedRegistry(MergedRegistry mergedRegistry) {
        this.mergedRegistry = mergedRegistry;
    }

    private MergedRegistry mergedRegistry;
    public void start(ArrayList<DateTimeAssistantAvailability>  ordered, ClientDay cl, int dayState){
        prepareAssistantHash(ordered);
        mainLoop(0,0,0,ordered,cl, dayState,new ReferentialBoolean(0));
    }
    private void prepareAssistantHash(ArrayList<DateTimeAssistantAvailability>  ordered){
        orderHashMap.clear();
        int i =0;
        for(DateTimeAssistantAvailability a : ordered){
            orderHashMap.put(a.getAssistantAvailability().getAssistant(),i);
            i++;
        }
    }
    public IntervalProcessing(AssistantMonthWorks workMonthI, Integer maxShiftLengthI, HashMap<UUID, Assistant> assistantHashMapI, MergedRegistry mergedRegistryI){
        workMonth =workMonthI;
        maxShiftLength = (maxShiftLengthI*60);
        assistantHashMap = assistantHashMapI;
        setMergedRegistry(mergedRegistryI);


    }
    private boolean endOfIntervals(int intervalIter , ClientDay cl){
        if(intervalIter == cl.getDayIntervalListUsefull().size()){
            return  true;
        }
        return false;
    }
    private boolean checkMergedAssistants(MergedRegistry mergedRegistry,ClientDay cl, ServiceInterval serv){
        MergedRecord merg = mergedRegistry.checkExistence(cl,serv);
        if(merg !=null && merg.getMergedIntervals().contains(serv)){
            if(serv ==(merg.getPrimaryInterval())){
                return false;
            }else{
                return true;
            }
        }
        return false;
    }
    private long[] findMostTimeAvailable(ServiceInterval s, int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl, AssistantWorkShift workShift, int dayState ) {
        long[] out = new long[]{0,0};
        for (int i = 0; i < av.size(); i++) {
            if(!intervalRequirements(s,av.get(i))){
                continue;
            }
            long workedMinutes = 0;
            long overlapMinutes = calculateOverlap(s, av.get(i));
            AssistantWorkShift w = workShiftCheck(i, av, dayState, cl);
            if (w.getAssistantID() != null) {
                workedMinutes =  ChronoUnit.MINUTES.between(w.getStart(), w.getEnd());
            }
                long checkedLength = overlapMinutes - workedMinutes;
                if (checkedLength > out[1]) {
                    out[1] = checkedLength;
                    out[0] = i;
                }

        }
        return out;
    }
    private long calculateOverlap(ServiceInterval s, DateTimeAssistantAvailability av){
        LocalDateTime maxStart = s.getStart().isAfter(av.getStart()) ? s.getStart() : av.getStart();
        LocalDateTime minEnd = s.getEnd().isBefore(av.getEnd()) ? s.getEnd() : av.getEnd();
        if (maxStart.isBefore(minEnd)) {
            return ChronoUnit.MINUTES.between(maxStart, minEnd);
        } else {
            return 0;
        }
    }
    private boolean checkComplete(ServiceInterval serv, DateTimeAssistantAvailability av){
        if((serv.getStart().isAfter(av.getStart()) || (serv.getStart().equals(av.getStart()))) && (serv.getEnd().isBefore(av.getEnd()) || (serv.getEnd().equals(av.getEnd())))){
            return  true;
        }else{
            return false;
        }

    }
    private void save(AssistantWorkShift workShift,ServiceInterval activeInterval,DateTimeAssistantAvailability activeAA, ClientDay cl){
            if (workShift.getAssistantID() == null || !workShift.getAssistantID().equals(activeAA.getAssistantAvailability().getAssistant())) {
                workShift.setUpFromInterval(activeInterval, cl, activeAA.getAssistantAvailability().getAssistant());
                workMonth.registerWorkDay(workShift);
            }
            AssistantWorkShift tempWork = workMonth.getFinishedWork().get(activeAA.getAssistantAvailability().getAssistant()).get((cl.getDayStatus()) ? cl.getDay() : (cl.getDay() + 100));
            if (tempWork != null && tempWork.getStart() != activeInterval.getStart() && tempWork.getEnd() != activeInterval.getEnd()) {
                workShift.setUpFromInterval(activeInterval, cl, activeAA.getAssistantAvailability().getAssistant());
            }
            activeInterval.setOverseeingAssistant(assistantHashMap.get(activeAA.getAssistantAvailability().getAssistant()));
    }
    private boolean splitToFitLoop(int intervalIter, int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState){
        //Set up local variables
        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        //Find assistant that can cover most of the required time.
        long[] mostAvailable = findMostTimeAvailable(activeInterval,AAIter,av,cl,workShift,dayState);
        AAIter = (int) mostAvailable[0];
        DateTimeAssistantAvailability activeAA = av.get(AAIter);
        workShiftCheck(AAIter,av,dayState,cl);
        //If no assistant can cover any amount of required time, return;
        if(mostAvailable[1]==0){
            return false;
        }
        //Check if available time doesn't exceed maximum allowed length of shift.
        if(mostAvailable[1] <= (Settings.getSettings().getMaxShiftLength()* 60L)) {
            //Use method for creating new interval that is covered by available assistant while maximizing coverage
            if(TimeComparator.beforeOrE(activeInterval.getStart(),activeAA.getStart()) && TimeComparator.afterOrE(activeAA.getEnd(),activeInterval.getEnd())&&TimeComparator.afterOrE(activeInterval.getEnd(),activeAA.getStart())){
                cl.addInterval(activeAA.getStart(), activeInterval.getEnd());
                return true;
            }else if (TimeComparator.afterOrE(activeAA.getEnd(),activeInterval.getStart()) && activeInterval.getEnd().isAfter(activeAA.getEnd())){
                cl.addInterval(activeInterval.getStart(), activeAA.getEnd());
                return true;

            }
        }else{
            //If the available time exceeds maximum shift create new interval that maximizes coverage without exceeding maximum shift length
            if(TimeComparator.beforeOrE(activeInterval.getStart(),activeAA.getStart())&& TimeComparator.afterOrE(activeInterval.getEnd(),activeAA.getStart()) && activeAA.getStart().plusHours(Settings.getSettings().getMaxShiftLength()).isBefore(activeAA.getEnd())){
                cl.addInterval(activeAA.getStart(), activeAA.getStart().plusHours(Settings.getSettings().getMaxShiftLength()));
                return true;
            }else if (activeAA.getEnd().isAfter(activeInterval.getStart()) && activeInterval.getEnd().isAfter(activeAA.getEnd())){
                cl.addInterval(activeInterval.getStart(), activeAA.getEnd());
                return true;
            }
        }
        return false;
    }
    private boolean lastLoop(int targetAAiter, int intervalIter, int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState,ReferentialBoolean solutionFound){
        //If all unchecked assistants were checked, return.
        if(AAIter==targetAAiter){
            return false;
        }
        //
        workShiftCheck(AAIter,av,dayState,cl);
        //Setting up local variables
        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        DateTimeAssistantAvailability activeAA = av.get(AAIter);
        //Check if current DateTimeAssistantAvailability fulfills all conditions.
        if(workShift.getWorkedMinutes()+activeInterval.getIntervalLength()<= maxShiftLength ) {
            if (checkComplete(activeInterval, activeAA)) {
                if (intervalRequirements(activeInterval, activeAA)) {
                    //Save result and report result
                    save(workShift, activeInterval, activeAA, cl);
                    //If the loop checked all available DateTimeAssistantAvailability, return positive result.
                    // This will prevent unwanted interactions.
                } else if (AAIter+1==targetAAiter || AAIter+1 != av.size()) {
                    solutionFound.setValue(2);
                }

                return true;
            }
        }
        //If all available DateTimeAssistantAvailability were not checked, try with next.
        if(AAIter+1 != av.size() ){
             lastLoop(targetAAiter,intervalIter,AAIter+1,av,cl,workShift,dayState,solutionFound);
        }
        return  false;
    }
    private boolean intervalRequirements(ServiceInterval serviceInterval, DateTimeAssistantAvailability activeAA){
        if(serviceInterval.isRequiresDriver() && !assistantHashMap.get(activeAA.getAssistantAvailability().getAssistant()).isDriver()){
            return false;
        }else{
            return  true;
        }
    }
    private boolean mainLoop(int startOfLoopAssistant, int intervalIter , int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl, int dayState, ReferentialBoolean solutionFound){
        //If interval iterator points out of bounds, exit.
        if(endOfIntervals(intervalIter,cl)){
            return false;
        }
        //Initialize local variables. ActiveInterval is currently examined interval.
        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        //activeAA denotes assistant availability that is currently tested as possible solution.
        DateTimeAssistantAvailability activeAA = av.get(AAIter);
        //workShift represents work already done in examined day by currently selected assistant.
        AssistantWorkShift workShift = workShiftCheck(AAIter, av, dayState, cl);
        //If there is assistant mandated by user, set him as assistant for interval
        if(assignedAssistantMain(intervalIter, AAIter, activeInterval, av, cl, dayState)){
            solutionFound.setValue(2);
           // return true;
        }
        if(checkMergedAssistants(getMergedRegistry(),cl,activeInterval)){
            solutionFound.setValue(2);
        }
        //If assistant can work entire length of service interval evaluate further.
        if(workShift.getWorkedMinutes()+activeInterval.getIntervalLength()<= maxShiftLength ){
            //if assistants availability covers entire service interval and any other assistant was not assigned.
            if (checkComplete(activeInterval, activeAA) && solutionFound.getValue()!=2) {
                //Assign current assistant as overseeing assistant and set solution as found
                if (intervalRequirements(activeInterval, activeAA)) {
                    save(workShift,activeInterval,activeAA,cl);
                    solutionFound.setValue(2);
                }

            }
        }
        //If the solution wasn't found and the assistant currently used isn't last
        if (av.size() != AAIter+1 && solutionFound.getValue()!=2) {
            //Try again for same interval with next assistant.
            if(mainLoop(startOfLoopAssistant,intervalIter,AAIter+1,av,cl, dayState,solutionFound)){
               // solutionFound.setValue(1);
            }
            //If assistant is last, the loop didn't start from first assistant and solution wasn't found.
        } else if (av.size() == AAIter+1 && startOfLoopAssistant !=0 && solutionFound.getValue()!=2) {
            //Backtrack through assistants through those that weren't checked
                if(lastLoop(startOfLoopAssistant,intervalIter,0,av,cl, workShift,dayState,solutionFound)){
                    solutionFound.setValue(2);
                }
        }
        //If good solution was not found.
        if(solutionFound.getValue()!=2){
            //Attempt to split the interval into more manageable size.
            if(splitToFitLoop(intervalIter,AAIter,av,cl,new AssistantWorkShift(),dayState))
            //if split was successful, try again for newly created interval
            {mainLoop(AAIter, intervalIter,AAIter,av,cl, dayState,solutionFound);}
            //if no solution was found.
        }
        if(solutionFound.getValue()!=1){
            //Continue with another interval.
                mainLoop(AAIter,intervalIter+1,findLastSuccess(intervalIter,cl),av,cl, dayState,new ReferentialBoolean(0));
            }
        //if no solution was found.
        if(solutionFound.getValue() !=2){
            solutionFound.setValue(1);
        }
        return true;
    }
    private int findLastSuccess(int intervalIter, ClientDay cl ){
        int tempIter = intervalIter;
        while(tempIter>=0){
            Assistant tested = cl.getDayIntervalListUsefull().get(tempIter).getOverseeingAssistant();
           if(tested!=null){
               return orderHashMap.get(tested.getID());
           }else{
               tempIter--;
           }
        }
        return 0;
    }

    private boolean assignedAssistantMain(int AAIter, int intervalIter, ServiceInterval activeInterval, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl, int dayState ){
        if(activeInterval.getAssignedAssistant() != null){
            AssistantWorkShift workShift = workShiftCheckAssigned(activeInterval.getAssignedAssistant(), av, dayState, cl);
            if(workShift.getAssistantID()==null ){
                workShift.setUpFromInterval(activeInterval,cl,activeInterval.getAssignedAssistant());
                workMonth.registerWorkDay(workShift);
            }
            AssistantWorkShift tempWork = workMonth.getFinishedWork().get(activeInterval.getAssignedAssistant()).get((dayState== 0)? cl.getDay() :(cl.getDay()+100));
            if(tempWork!=null && tempWork.getStart() != activeInterval.getStart() &&tempWork.getEnd() != activeInterval.getEnd()){
                workShift.setUpFromInterval(activeInterval,cl,activeInterval.getAssignedAssistant());
            }
            activeInterval.setOverseeingAssistant(assistantHashMap.get(activeInterval.getAssignedAssistant()));
            mainLoop(AAIter,intervalIter+1,AAIter,av,cl, dayState,new ReferentialBoolean(0));
            return true;
        }else{
            return false;
        }
    }
    private AssistantWorkShift workShiftCheck(int AAIter, ArrayList<DateTimeAssistantAvailability> av,int dayState,ClientDay cl){
        AssistantWorkShift check = workMonth.getFinishedWork().get(av.get(AAIter).getAssistantAvailability().getAssistant()).get((dayState == 0) ? cl.getDay(): cl.getDay()+100 );
        AssistantWorkShift workShift;
        if(check!= null){
            workShift = check;
        }else{
            workShift = new AssistantWorkShift();
        }
        return workShift;
    }
    private AssistantWorkShift workShiftCheckAssigned(UUID assignedAssistant, ArrayList<DateTimeAssistantAvailability> av,int dayState,ClientDay cl){
        AssistantWorkShift check = workMonth.getFinishedWork().get(assignedAssistant).get((dayState == 0) ? cl.getDay(): cl.getDay()+100 );
        AssistantWorkShift workShift;
        if(check!= null){
            workShift = check;
        }else{
            workShift = new AssistantWorkShift();
        }
        return workShift;
    }
}