package graphics.sorter.Vacations;

import graphics.sorter.Structs.ServiceInterval;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;

public class VacationList  {
private HashMap<Integer,HashMap<Integer, ArrayList<Vacation>>> yearMap = new HashMap<Integer,HashMap<Integer, ArrayList<Vacation>>>();

public void add(Vacation vac){
    int year = vac.getStart().getYear();
    int month = vac.getStart().getMonthValue();
    HashMap<Integer, ArrayList<Vacation>> yearM = yearMap.get(year);
    ArrayList<ArrayList<Integer>> iter = yearsAndMonths(vac);
    if(yearM != null){
        addMonth(vac,month,yearM);
    }else{
    yearMap.put(year,new HashMap<Integer, ArrayList<Vacation>>());
    }
}
private void addMonth(Vacation vac,int month,HashMap<Integer, ArrayList<Vacation>> yearM){
    ArrayList<Vacation> monthM = yearM.get(month);
    if(monthM != null){
        monthM.add(vac);
    }else{
        yearM.put(month,new ArrayList<Vacation>(Arrays.asList(vac)));
    }
}
private ArrayList<ArrayList<Integer>> yearsAndMonths(Vacation vac){
    ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>(Arrays.asList(new ArrayList<Integer>(),new ArrayList<Integer>()));
    IntStream.rangeClosed(vac.getStart().getYear(), vac.getEnd().getYear()).forEach(n->output.get(0).add(n));
    IntStream.rangeClosed(vac.getStart().getMonthValue(), vac.getEnd().getMonthValue()).forEach(n->output.get(1).add(n));
    return output;
}
}
