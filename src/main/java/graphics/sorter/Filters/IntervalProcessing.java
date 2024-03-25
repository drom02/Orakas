package graphics.sorter.Filters;

import graphics.sorter.Assistant;
import graphics.sorter.AssistantAvailability.DateTimeAssistantAvailability;
import graphics.sorter.Settings;
import graphics.sorter.Structs.ClientDay;
import graphics.sorter.Structs.ServiceInterval;

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
    public IntervalProcessing(AssistantMonthWorks workMonthI, Integer maxShiftLengthI, HashMap<UUID, Assistant> assistantHashMapI){
        workMonth =workMonthI;
        maxShiftLength = (maxShiftLengthI*60);
        assistantHashMap = assistantHashMapI;



    }
    private void intervalAssignment(ClientDay cl, ArrayList<UUID> availableAssistantsID, AssistantMonthWorks workMonth ){
        AssistantWorkShift workShift= new AssistantWorkShift();
        for(int i =0; i < cl.getDayIntervalListUsefull().size();i++){
            if((workShift.getWorkedMinutes() + cl.getDayIntervalListUsefull().get(i).serviceIntervalLength()) < Settings.getSettings().getMaxShiftLength()){
            }else{
            }
        }
        // AssistantWorkShift workShift= new AssistantWorkShift(availableAssistantsID.get(0),cl);
        workMonth.registerWorkDay(workShift);
    }
    private void endOfAssistants(int intervalIter , int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState){
        if(AAIter == av.size()){
            evaluateInterval(intervalIter+1,0,av,  cl,workShift,dayState,false);
        }
    }
    private boolean endOfIntervals(int intervalIter , ClientDay cl){
        if(intervalIter == cl.getDayIntervalListUsefull().size()){
            return  true;
        }
        return false;
    }
    private AssistantWorkShift workShiftCheck(int AAIter, ArrayList<DateTimeAssistantAvailability> av,int dayState,ClientDay cl, AssistantWorkShift workShift){
        AssistantWorkShift check = workMonth.getFinishedWork().get(av.get(AAIter).getAssistantAvailability().getAssistant()).get((dayState == 0) ? cl.getDay(): cl.getDay()+100 );
        if(check!= null){
            workShift = check;
        }
        return workShift;
    }
    public void evaluateInterval(int intervalIter , int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState, boolean secondRun){
         boolean solutionFound = false;
        //Various stop conditions and preparatory operations
        //If the previous interval was last, return;
        if(endOfIntervals(intervalIter,cl)){
            return;
        }
        if(intervalIter < cl.getDayIntervalListUsefull().size()){
        //If the selescted assistant already worked this shift, load his workShift;
        workShiftCheck(AAIter,av,dayState,cl,workShift);
        //
        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);

        if(workShift.getWorkedMinutes()+activeInterval.getIntervalLength()<= maxShiftLength ) {
            solutionFound = singleRun(intervalIter,AAIter,av,cl,workShift,dayState,secondRun);
        }else if (AAIter+1 != av.size()){
            //In case the shift is too long, try another assistant
            evaluateInterval(intervalIter,AAIter+1,av,cl,new AssistantWorkShift(),dayState,secondRun);
        }else if (AAIter+1 == av.size()) {
            //If end of assistants is reached, split to fit .
                splitToFit(intervalIter,AAIter,av,cl,new AssistantWorkShift(),dayState,secondRun);
        }

            lastRun(intervalIter,0,av,cl,new AssistantWorkShift(),dayState,secondRun);
        }
        if(AAIter+1 == av.size()){
            evaluateInterval(intervalIter+1,0,av,  cl,workShift,dayState,false);
        }
    }
    private int findMostTimeAvailable(ServiceInterval s,int AAIter,ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState ){
        int outIter = 0;
        long maxLength = 0;
        for(int i =0; i < AAIter;i++){
           long overlapMinutes = calculateOverlap(s,av.get(i));
            AssistantWorkShift w = workShiftCheck(i,av,dayState,cl,workShift);
            if(w.getAssistantID()!=null){
                long checkedLength = overlapMinutes - ChronoUnit.MINUTES.between(w.getStart(),w.getEnd());
                if(checkedLength> maxLength){
                    maxLength =checkedLength;
                    outIter = i;
                }
            }
        }
        return outIter;
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
    private void splitToFit(int intervalIter, int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState, boolean secondRun){
        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        AAIter = findMostTimeAvailable(activeInterval,AAIter,av,cl,workShift,dayState);
        DateTimeAssistantAvailability activeAA = av.get(AAIter);
        workShiftCheck(AAIter,av,dayState,cl,workShift);
        if(activeInterval.getStart().isBefore(activeAA.getEnd())){
            cl.addInterval(activeAA.getStart(), activeInterval.getEnd());
        }else{
            cl.addInterval(activeAA.getEnd(), activeInterval.getEnd());
        }

        if(cl.getDayIntervalListUsefull().size()< intervalIter){
            singleRun(intervalIter, AAIter, av, cl, new AssistantWorkShift(), dayState, secondRun);
        }

    }

    private boolean singleRun(int intervalIter, int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState, boolean secondRun){
        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        if(assignedAssistant(intervalIter, AAIter, workShift, activeInterval, av, cl, dayState)){
            return true;
        }
        DateTimeAssistantAvailability activeAA = av.get(AAIter);
        workShiftCheck(AAIter,av,dayState,cl,workShift);
        if(workShift.getWorkedMinutes()+activeInterval.getIntervalLength()<= maxShiftLength ) {
            if (checkComplete(activeInterval, activeAA)) {
                if (workShift.getAssistantID() == null || !workShift.getAssistantID().equals(activeAA.getAssistantAvailability().getAssistant())) {
                    workShift.setUpFromInterval(activeInterval, cl, activeAA.getAssistantAvailability().getAssistant());
                    workMonth.registerWorkDay(workShift);
                }
                AssistantWorkShift tempWork = workMonth.getFinishedWork().get(activeAA.getAssistantAvailability().getAssistant()).get((dayState == 0) ? cl.getDay() : (cl.getDay() + 100));
                if (tempWork != null && tempWork.getStart() != activeInterval.getStart() && tempWork.getEnd() != activeInterval.getEnd()) {
                    workShift.setUpFromInterval(activeInterval, cl, activeAA.getAssistantAvailability().getAssistant());
                }
                activeInterval.setOverseeingAssistant(assistantHashMap.get(activeAA.getAssistantAvailability().getAssistant()));
                evaluateInterval(intervalIter + 1, AAIter, av, cl, workShift, dayState, secondRun);
                return true;
            } else if (checkIncompleteLeft(activeInterval, activeAA)) {
                cl.addInterval(cl.getDayIntervalListUsefull().get(intervalIter).getStart(), activeAA.getEnd());
                singleRun(intervalIter, AAIter, av, cl, new AssistantWorkShift(), dayState, secondRun);
                return false;
            } else if (checkIsAfter(activeInterval, activeAA) && AAIter + 1 != av.size()) {
                singleRun(intervalIter, AAIter + 1, av, cl, new AssistantWorkShift(), dayState, secondRun);
            } else if (AAIter + 1 != av.size()) {
                evaluateInterval(intervalIter, AAIter + 1, av, cl, new AssistantWorkShift(), dayState, secondRun);

            }else{
                lastRun(intervalIter, 0, av, cl, new AssistantWorkShift(), dayState, secondRun);
            }
            return false;
        }
        return  false;
    }
    private boolean lastRun(int intervalIter, int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState, boolean secondRun){
        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        if(assignedAssistant(intervalIter, AAIter, workShift, activeInterval, av, cl, dayState)){
            return true;
        }
        if(activeInterval.getOverseeingAssistant() == null){
            DateTimeAssistantAvailability activeAA = av.get(AAIter);
            workShiftCheck(AAIter,av,dayState,cl,workShift);
            if(workShift.getWorkedMinutes()+activeInterval.getIntervalLength()<= maxShiftLength ) {
                //
                if(checkComplete(activeInterval, activeAA)){
                    if(workShift.getAssistantID()==null ||!workShift.getAssistantID().equals(activeAA.getAssistantAvailability().getAssistant()) ){
                        workShift.setUpFromInterval(activeInterval,cl,activeAA.getAssistantAvailability().getAssistant());
                        workMonth.registerWorkDay(workShift);
                    }
                    AssistantWorkShift tempWork = workMonth.getFinishedWork().get(activeAA.getAssistantAvailability().getAssistant()).get((dayState== 0)? cl.getDay() :(cl.getDay()+100));
                    if(tempWork!=null && tempWork.getStart() != activeInterval.getStart() &&tempWork.getEnd() != activeInterval.getEnd()){
                        workShift.setUpFromInterval(activeInterval,cl,activeAA.getAssistantAvailability().getAssistant());
                    }
                    activeInterval.setOverseeingAssistant(assistantHashMap.get(activeAA.getAssistantAvailability().getAssistant()));
                    evaluateInterval(intervalIter + 1, AAIter, av, cl, workShift, dayState, secondRun);

                }else if (checkIncompleteLeft(activeInterval, activeAA)) {
                    cl.addInterval(cl.getDayIntervalListUsefull().get(intervalIter).getStart(), activeAA.getEnd());
                    lastRun(intervalIter, AAIter, av, cl, new AssistantWorkShift(), dayState, secondRun);
                    return false;
                }
            }else if (AAIter+1 != av.size()){
                lastRun(intervalIter,AAIter+1,av,cl,workShift,dayState,secondRun);
            }
            return false;
        }
       return false;
    }
    private DateTimeAssistantAvailability getDateTimeAssis(UUID id, ArrayList<DateTimeAssistantAvailability> av){
        for(int i =0; i < av.size();i++){
            if(av.get(i).getAssistantAvailability().getAssistant().equals(id)){
                return  av.get(i);
            }
        }
        return null;
    }

    private boolean assignedAssistant(int AAIter, int intervalIter, AssistantWorkShift workShift,ServiceInterval activeInterval,ArrayList<DateTimeAssistantAvailability> av,ClientDay cl, int dayState ){

            if(activeInterval.getAssignedAssistant() != null){
                System.out.println("assigned assistant");
                //DateTimeAssistantAvailability activeAA = getDateTimeAssis(activeInterval.getAssignedAssistant(),av);
                workShiftCheck(AAIter,av,dayState,cl,workShift);
                if(workShift.getAssistantID()==null ){
                    workShift.setUpFromInterval(activeInterval,cl,activeInterval.getAssignedAssistant());
                    workMonth.registerWorkDay(workShift);
                }
                AssistantWorkShift tempWork = workMonth.getFinishedWork().get(activeInterval.getAssignedAssistant()).get((dayState== 0)? cl.getDay() :(cl.getDay()+100));
                if(tempWork!=null && tempWork.getStart() != activeInterval.getStart() &&tempWork.getEnd() != activeInterval.getEnd()){
                    workShift.setUpFromInterval(activeInterval,cl,activeInterval.getAssignedAssistant());
                }
                activeInterval.setOverseeingAssistant(assistantHashMap.get(activeInterval.getAssignedAssistant()));
                evaluateInterval(intervalIter + 1, AAIter, av, cl, workShift, dayState, secondRun);
                return true;
            }else{
                return false;
            }
    }
    private boolean checkComplete(ServiceInterval serv, DateTimeAssistantAvailability av){
        if((serv.getStart().isAfter(av.getStart()) || (serv.getStart().equals(av.getStart()))) && (serv.getEnd().isBefore(av.getEnd()) || (serv.getEnd().equals(av.getEnd())))){
            return  true;
        }else{
            return false;
        }

    }
    private boolean checkIncompleteLeft(ServiceInterval serv, DateTimeAssistantAvailability av){
        if((serv.getStart().isBefore(av.getEnd()) &&  (serv.getStart().isAfter(av.getStart()) || (serv.getStart().equals(av.getStart())  ))) && (serv.getEnd().isAfter(av.getEnd()))){
            return  true;
        }else{
            return false;
        }
    }
    private boolean checkIncompleteRight(ServiceInterval serv, DateTimeAssistantAvailability av){
        if((serv.getStart().isBefore(av.getStart())) && (serv.getEnd().isBefore(av.getEnd()) || (serv.getEnd().equals(av.getEnd())))){
            return  true;
        }else{
            return false;
        }
    }
    private boolean checkIsAfter(ServiceInterval serv, DateTimeAssistantAvailability av){
        if(((serv.getStart().isAfter(av.getEnd()) ||serv.getStart().isEqual(av.getEnd()) ))){
            return  true;
        }else{
            return false;
        }
    }
}
