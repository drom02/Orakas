package Orakas.controllers;
/*
Class is used for management of parts of AssistantViewController.
Specifically, for controlling types of assistant shifts.
 */

import Orakas.Humans.Assistant;
import Orakas.Settings;
import Orakas.workHoursAllocation.WorkHoursCalcul;
import javafx.scene.control.TextField;

public class WorkedHoursCheck {
    WorkedHoursCheck(){

    }
    public static void check(double input, Assistant selectedAssistant, TextField workField){
        switch (selectedAssistant.getContractType()){
            case "HPP":
                HPPCheck( input,selectedAssistant, workField);
                break;
            case "DPP":
                DPPCheck(input,selectedAssistant, workField);
                break;
            case "DPČ":
                DPCCheck(input,selectedAssistant, workField);
                break;
            case "HPP-Vlastní":
                custom(input,selectedAssistant);
                break;
        }
    }
    public static void HPPCheck(double input, Assistant selectedAssistant, TextField workField){
        if(input<=1 && input>=0.25){
            selectedAssistant.setContractTime(input);
        }else {
            selectedAssistant.setContractTime((input < 0.25) ? 0.25: 1);
        }
    }
    public static void DPPCheck(double input, Assistant selectedAssistant, TextField workField){
        double maxWork = WorkHoursCalcul.workDaysCalcul(Settings.getSettings().getCurrentYear(), Settings.getSettings().getCurrentMonth(), Settings.getSettings().getStandardWorkDay(), selectedAssistant.getID(),1);
        if(input<=maxWork){
            selectedAssistant.setContractTime(input);
        }else {
            selectedAssistant.setContractTime(maxWork);
        }
    }
    public static void DPCCheck(double input, Assistant selectedAssistant, TextField workField){
        double maxWork = WorkHoursCalcul.workDaysCalcul(Settings.getSettings().getCurrentYear(), Settings.getSettings().getCurrentMonth(), Settings.getSettings().getStandardWorkDay(), selectedAssistant.getID(),1);
        if(input<=maxWork*0.5){
            selectedAssistant.setContractTime(input);
        }else {
            selectedAssistant.setContractTime(maxWork*0.5);
        }
    }
    public static void custom(double input, Assistant selectedAssistant){
            selectedAssistant.setContractTime(input);
    }


}
