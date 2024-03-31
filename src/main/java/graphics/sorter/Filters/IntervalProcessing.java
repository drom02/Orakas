package graphics.sorter.Filters;

import graphics.sorter.Assistant;
import graphics.sorter.AssistantAvailability.DateTimeAssistantAvailability;
import graphics.sorter.Mediator.ReferentialBoolean;
import graphics.sorter.Settings;
import graphics.sorter.Structs.ClientDay;
import graphics.sorter.Structs.ServiceInterval;
import graphics.sorter.Structs.TimeComparator;

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
    public void start(ArrayList<DateTimeAssistantAvailability>  ordered,ClientDay cl, int dayState){
        mainLoop(0,0,0,ordered,cl, dayState,false,new ReferentialBoolean(0));
    }
    public IntervalProcessing(AssistantMonthWorks workMonthI, Integer maxShiftLengthI, HashMap<UUID, Assistant> assistantHashMapI){
        workMonth =workMonthI;
        maxShiftLength = (maxShiftLengthI*60);
        assistantHashMap = assistantHashMapI;



    }
    private boolean endOfIntervals(int intervalIter , ClientDay cl){
        if(intervalIter == cl.getDayIntervalListUsefull().size()){
            return  true;
        }
        return false;
    }

    private long[] findMostTimeAvailable(ServiceInterval s,int AAIter,ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState ) {
        long[] out = new long[]{0,0};
        for (int i = 0; i < av.size(); i++) {
            long overlapMinutes = calculateOverlap(s, av.get(i));
            AssistantWorkShift w = workShiftCheck(i, av, dayState, cl);
            if (w.getAssistantID() != null) {
                long checkedLength = overlapMinutes - ChronoUnit.MINUTES.between(w.getStart(), w.getEnd());
                if (checkedLength > out[1]) {
                    out[1] = checkedLength;
                    out[0] = i;
                }
            }else {
                if (overlapMinutes > out[1]) {
                    out[1] = overlapMinutes;
                    out[0] = i;
                }
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
    private boolean splitToFitLoop(int startOfLoopAssistant,int intervalIter, int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState,boolean tooLong, ReferentialBoolean solutionFound){
        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        long[] mostAvailable = findMostTimeAvailable(activeInterval,AAIter,av,cl,workShift,dayState);
        AAIter = (int) mostAvailable[0];
        DateTimeAssistantAvailability activeAA = av.get(AAIter);
        workShiftCheck(AAIter,av,dayState,cl);
        if(mostAvailable[1]==0){
            return false;
        }
        if(mostAvailable[1] <= (Settings.getSettings().getMaxShiftLength()* 60L)) {
            if(TimeComparator.beforeOrE(activeInterval.getStart(),activeAA.getStart()) && TimeComparator.afterOrE(activeAA.getEnd(),activeInterval.getEnd())&&TimeComparator.afterOrE(activeInterval.getEnd(),activeAA.getStart())){
                cl.addInterval(activeAA.getStart(), activeInterval.getEnd());
            }else if (TimeComparator.afterOrE(activeAA.getEnd(),activeInterval.getStart()) && activeInterval.getEnd().isAfter(activeAA.getEnd())){
                cl.addInterval(activeInterval.getStart(), activeAA.getEnd());
            }
        }else{
            //TODO case if the available shift is too long 
            if(TimeComparator.beforeOrE(activeInterval.getStart(),activeAA.getStart())&& TimeComparator.afterOrE(activeInterval.getEnd(),activeAA.getStart()) && activeAA.getStart().plusHours(Settings.getSettings().getMaxShiftLength()).isBefore(activeAA.getEnd())){
                cl.addInterval(activeAA.getStart(), activeAA.getStart().plusHours(Settings.getSettings().getMaxShiftLength()));
            }else if (activeAA.getEnd().isAfter(activeInterval.getStart()) && activeInterval.getEnd().isAfter(activeAA.getEnd())){
                cl.addInterval(activeInterval.getStart(), activeAA.getEnd());
            }else{
                System.out.println("asdasdadsasdadsadsadsasdasdasdadsasdasdasdadsadasdasdadadsadsada");
            }
        }
        return true;
    }
    private boolean lastLoop(int targetAAiter, int intervalIter, int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState,ReferentialBoolean solutionFound){
        if(AAIter==targetAAiter){
            return false;
        }
        workShiftCheck(AAIter,av,dayState,cl);
        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        DateTimeAssistantAvailability activeAA = av.get(AAIter);
        if(workShift.getWorkedMinutes()+activeInterval.getIntervalLength()<= maxShiftLength ) {
            if (checkComplete(activeInterval, activeAA)) {
                save(workShift, activeInterval, activeAA, cl);
                solutionFound.setValue(2);
                return true;
            }
        }
        if(AAIter+1 != av.size() ){
             lastLoop(targetAAiter,intervalIter,AAIter+1,av,cl,workShift,dayState,solutionFound);
        }
        return  false;
    }
    private boolean mainLoop(int startOfLoopAssistant, int intervalIter , int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl, int dayState, boolean noSplitRun, ReferentialBoolean solutionFound){
        boolean tooLong = false;
        if(endOfIntervals(intervalIter,cl)){
            return false;
        }
        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        DateTimeAssistantAvailability activeAA = av.get(AAIter);
        AssistantWorkShift workShift = workShiftCheck(AAIter, av, dayState, cl);

        if(assignedAssistantMain(intervalIter, AAIter, activeInterval, av, cl, dayState)){
            solutionFound.setValue(2);
            return true;
        }

        if(workShift.getWorkedMinutes()+activeInterval.getIntervalLength()<= maxShiftLength ){
            if (checkComplete(activeInterval, activeAA) && solutionFound.getValue()!=2) {
                save(workShift,activeInterval,activeAA,cl);
                solutionFound.setValue(2);
            }
        }else{
            tooLong =true;
        }

        if (av.size() != AAIter+1 && solutionFound.getValue()!=2) {
            if(mainLoop(startOfLoopAssistant,intervalIter,AAIter+1,av,cl, dayState,noSplitRun,solutionFound)){
                solutionFound.setValue(2);
            }
        } else if (av.size() == AAIter+1 && startOfLoopAssistant !=0 && solutionFound.getValue()!=2) {
                if(lastLoop(startOfLoopAssistant,intervalIter,0,av,cl, workShift,dayState,solutionFound)){
                    solutionFound.setValue(2);
                }
        }

        if(noSplitRun==false&& solutionFound.getValue()!=2){
            if(splitToFitLoop(startOfLoopAssistant,intervalIter,AAIter,av,cl,new AssistantWorkShift(),dayState,tooLong,solutionFound))
            {mainLoop(AAIter, (intervalIter-1>=0)? intervalIter-1: intervalIter,AAIter,av,cl, dayState,false,solutionFound);}
        }else if(solutionFound.getValue()!=1){
                mainLoop(AAIter,intervalIter+1,AAIter,av,cl, dayState,false,new ReferentialBoolean(0));
            }

            solutionFound.setValue(1);


        return true;
    }
    private boolean assignedAssistantMain(int AAIter, int intervalIter, ServiceInterval activeInterval, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl, int dayState ){
        if(activeInterval.getAssignedAssistant() != null){
            System.out.println("assigned assistant");
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
            mainLoop(AAIter,intervalIter+1,AAIter,av,cl, dayState,false,new ReferentialBoolean(0));
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