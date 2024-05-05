package Orakas.controllers;

import Orakas.Structs.TimeStructs.ClientDay;
import Orakas.Structs.TimeStructs.ServiceInterval;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.HashMap;
/*
Class responsible for coloring GUI of the main screen according to the results.
 */
public class DisplayMainResults {
    /*
    Change color of all TextFlows that make up the main view month overview.
     */
        public static void colorResult( HashMap<TextFlow, ClientDay> textClientIndex ){
            for(TextFlow fl : textClientIndex.keySet()){
                ClientDay cl =textClientIndex.get(fl);
                if(!evaluateAllGood(cl.getDayIntervalListUsefull())){
                    if(evaluatePartiallyGood(cl.getDayIntervalListUsefull())){
                        fl.getStyleClass().add("result-partial");
                    }else{
                        fl.getStyleClass().add("result-negative");
                    }
                }else{
                    fl.getStyleClass().add("result-positive");
                }
            }
        }
    /*
    Remove colors from all TextFlows that make up the main view month overview.
    */
    public static void clearResults( HashMap<TextFlow, ClientDay> textClientIndex ){
        for(TextFlow fl : textClientIndex.keySet()){
            for(int i=1; i<fl.getStyleClass().size();i++)
            fl.getStyleClass().remove(i);
        }
    }
    /*
    Check if all required intervals are covered
     */
        private static boolean evaluateAllGood(ArrayList<ServiceInterval> intervals){
            for(ServiceInterval serv:intervals){
                if(serv.getOverseeingAssistant()==null){
                    return false;
                }
            }
            return true;
        }
        //Runs only if evaluateAllGood returned false, meaning it returning true means at least one is correct
    private static boolean evaluatePartiallyGood(ArrayList<ServiceInterval> intervals){
        for(ServiceInterval serv:intervals){
            if(serv.getOverseeingAssistant()!=null){
                 return true;
            }
        }
        return false;
    }
}
