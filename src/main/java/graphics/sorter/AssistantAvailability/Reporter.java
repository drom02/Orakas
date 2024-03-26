package graphics.sorter.AssistantAvailability;

import graphics.sorter.Filters.AssistantMonthWorks;
import graphics.sorter.Filters.AssistantWorkShift;

import java.util.HashMap;
import java.util.UUID;

public class Reporter {
    public Reporter(AssistantMonthWorks workedMonth){
        for(UUID id : workedMonth.getFinishedWork().keySet()){
                HashMap<Integer, AssistantWorkShift> w = workedMonth.getFinishedWork().get(id);
                    System.out.println("Asistent " + id );
                    int totaWorked = 0;
                for(Integer in :workedMonth.getFinishedWork().get(id).keySet() ){
                    AssistantWorkShift wok = w.get(in);
                    System.out.println("Den " + wok.getDay() );
                    totaWorked += wok.getWorkedMinutes();
                    System.out.println((in>100) ? "noc" : "den");
                    System.out.println("Odpracováno " + wok.getWorkedMinutes());

                }
            System.out.println("Celkem " + totaWorked/60);
            }

        }

}