package Orakas.workHoursAllocation;
import Orakas.Database.Database;
import Orakas.Vacations.Vacation;
import Orakas.Vacations.VacationTemp;
import de.focus_shift.jollyday.core.Holiday;
import de.focus_shift.jollyday.core.HolidayManager;
import de.focus_shift.jollyday.core.ManagerParameters;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import static de.focus_shift.jollyday.core.HolidayCalendar.CZECH_REPUBLIC;

public class WorkHoursCalcul {

    public WorkHoursCalcul(){
    //countWorkDays(2025,1,Month.of(1).length(Year.isLeap(2025)));
        //System.out.println(workDaysCalcul(25,1,7.5));
    }
    public static double workDaysCalcul(int year, int month, double standardWorkDay, UUID assistant, double contractLength ){
        int length = Month.of(month).length(Year.isLeap(year));
        return countWorkDays(year, month,length, assistant) * standardWorkDay* contractLength;
    }
    public static Set<Holiday> getHolidaysOfMonth(int year, int month, int monthLength){
        HolidayManager holidayManager = HolidayManager.getInstance(ManagerParameters.create(CZECH_REPUBLIC));
        Set<Holiday> holidays = holidayManager.getHolidays(LocalDate.of(year, month, 1), LocalDate.of(year, month, monthLength));
        return holidays;
    }
    public static boolean isWorkday(LocalDate date){
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }
    private static int countWorkDays(int year, int month, int monthLength,UUID assistant) {
        ArrayList<Vacation> relevantVac = relevantVacation(year,month, Database.loadVacation(assistant));
        Set<LocalDate> holidays = new HashSet<LocalDate>();
        for(Holiday hol : getHolidaysOfMonth(year, month,monthLength)){
            holidays.add(hol.getDate());
        }
        ArrayList<LocalDate> dayDates = new ArrayList<>(31);
        Month workedMonth = Month.of(month);
        for(int i = 1; i <= monthLength;i++){
            LocalDate date = LocalDate.of(year, workedMonth, i);
            if(isWorkday(date) && !holidays.contains(date)){
                if(!relevantVac.isEmpty() ){
                    if(relevantVac.stream()
                            .noneMatch(vacation -> !date.isBefore(vacation.getStart()) && !date.isAfter(vacation.getEnd()))){
                        dayDates.add(date);
                    }
                }else{
                    dayDates.add(date);
                }



            }
        }
        return dayDates.size();
    }
    private static ArrayList<Vacation> relevantVacation(int year, int month, VacationTemp vac){
       ArrayList<Vacation> relevantList = vac.getTempVacation().stream().filter(c-> c.isRemovesWorkDays()).collect(Collectors.toCollection(ArrayList:: new));
       ArrayList<Vacation> relevantYears = relevantList.stream().filter(c-> (c.getStart().getYear() <= year && year <= c.getEnd().getYear())).collect(Collectors.toCollection(ArrayList:: new));
       ArrayList<Vacation> relevantMonth = relevantYears.stream().filter(c-> (c.getStart().getMonthValue() <= month && month <= c.getEnd().getMonthValue())).collect(Collectors.toCollection(ArrayList:: new));
       return  relevantMonth;


    }
}
