package graphics.sorter.Filters;

import graphics.sorter.Assistant;
import graphics.sorter.AssistantAvailability.DateTimeAssistantAvailability;
import graphics.sorter.Settings;
import graphics.sorter.Structs.ClientDay;
import graphics.sorter.Structs.ServiceInterval;

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
        maxShiftLength = maxShiftLengthI;
        assistantHashMap = assistantHashMapI;



    }
    private void intervalAssignment(ClientDay cl, ArrayList<UUID> availableAssistantsID, AssistantMonthWorks workMonth ){
        AssistantWorkShift workShift= new AssistantWorkShift();
        for(int i =0; i < cl.getDayIntervalListUsefull().size();i++){
            if((workShift.getWorkedHours() + cl.getDayIntervalListUsefull().get(i).serviceIntervalLength()) < Settings.getSettings().getMaxShiftLength()){
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
    public void evaluateInterval(int intervalIter , int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState, boolean secondRun){
        //Various stop conditions and preparatory operations


        if(endOfIntervals(intervalIter,cl)){
            return;
        }

        if(AAIter+1 == av.size()){
            evaluateInterval(intervalIter+1,0,av,  cl,workShift,dayState,false);
        }
        if(AAIter+1 > av.size()){
            return;
        }

        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        if(activeInterval.getEnd().getHour() ==23){
            System.out.println();
        }
        //Todo finish maxShiftLengthLimitations
       // DateTimeAssistantAvailability activeAA = av.get(AAIter);
        if(workShift.getWorkedHours()+activeInterval.getIntervalLength()<= maxShiftLength ) {
            if(!singleRun(intervalIter,AAIter,av,cl,workShift,dayState,secondRun)){
                lastRun(intervalIter,0,av,cl,new AssistantWorkShift(),dayState,secondRun);
            }
        }else if (AAIter+1 != av.size()){
            if(!singleRun(intervalIter,AAIter+1,av,cl,new AssistantWorkShift(),dayState,secondRun)){
                lastRun(intervalIter,0,av,cl,new AssistantWorkShift(),dayState,secondRun);
            }
            /*
            if(AAIter+1 == av.size()){
                if(secondRun == true){
                    return;
                }else{
                    singleRun(intervalIter,0,av,cl,new AssistantWorkShift(),dayState,true);
                }
            }else{
               // evaluateInterval(intervalIter,AAIter+1,av,cl,new AssistantWorkShift(),dayState,secondRun);
            }
             */
        }
        else{
            lastRun(intervalIter,0,av,cl,new AssistantWorkShift(),dayState,secondRun);
        }
        System.out.println("untreated");

    }


    private boolean singleRun(int intervalIter, int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState, boolean secondRun){

        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        DateTimeAssistantAvailability activeAA = av.get(AAIter);
        if(checkComplete(activeInterval, activeAA)){
            if(workShift.getAssistantID()==null ||!workShift.getAssistantID().equals(activeAA.getAssistantAvailability().getAssistant()) ){
                workShift.setUpFromInterval(activeInterval,cl,activeAA.getAssistantAvailability().getAssistant());
                workMonth.registerWorkDay(workShift);
            }
            AssistantWorkShift tempWork = workMonth.getFinishedWork().get(activeAA.getAssistantAvailability().getAssistant()).get((dayState== 0)? cl.getDay() :(cl.getDay()+100));
            if(tempWork!=null && tempWork.getStart() != activeInterval.getStart() &&tempWork.getEnd() != activeInterval.getEnd()){
                workShift.setUpFromInterval(activeInterval,cl,activeAA.getAssistantAvailability().getAssistant());
                System.out.println("test");
            }
            activeInterval.setOverseeingAssistant(assistantHashMap.get(activeAA.getAssistantAvailability().getAssistant()));
            evaluateInterval(intervalIter+1,AAIter,av,cl,workShift,dayState,secondRun);
            return true;
        } else if (checkIncompleteLeft(activeInterval,activeAA)) {
            cl.addInterval(cl.getDayIntervalListUsefull().get(intervalIter).getStart(),activeAA.getEnd());
            singleRun(intervalIter,AAIter,av,cl, new AssistantWorkShift(),dayState,secondRun);
            return false;
        }  else if (checkIsAfter(activeInterval,activeAA)){
            evaluateInterval(intervalIter,AAIter+1,av,cl,workShift,dayState,secondRun);
            return false;
        }else if(AAIter+1 != av.size()){
            evaluateInterval(intervalIter,AAIter+1,av,cl,workShift,dayState,secondRun);
            return false;
        }
        return false;
    }
    private void lastRun(int intervalIter, int AAIter, ArrayList<DateTimeAssistantAvailability> av, ClientDay cl,AssistantWorkShift workShift, int dayState, boolean secondRun){

        ServiceInterval activeInterval = cl.getDayIntervalListUsefull().get(intervalIter);
        DateTimeAssistantAvailability activeAA = av.get(AAIter);
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
        } else if (AAIter+1 != av.size()){
            lastRun(intervalIter,AAIter+1,av,cl,workShift,dayState,secondRun);
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
