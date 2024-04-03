package Orakas.AssistantAvailability;

import Orakas.Filters.AssistantMonthWorks;
import Orakas.Filters.AssistantWorkShift;
import Orakas.Filters.WorkShiftSubInterval;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    public static  HashMap<UUID,Double> totalWorked(AssistantMonthWorks workedMonth){
        HashMap<UUID,Double>  output = new HashMap<UUID,Double>();
        for(UUID id : workedMonth.getFinishedWork().keySet()){
            HashMap<Integer, AssistantWorkShift> w = workedMonth.getFinishedWork().get(id);
            Double totaWorked = 0.0;
            for(Integer in :workedMonth.getFinishedWork().get(id).keySet() ){
                AssistantWorkShift wok = w.get(in);
                totaWorked += wok.getWorkedMinutes();
            }
            output.put(id,totaWorked/60);
        }
        return output;
    }
    public static void reportAll(AssistantMonthWorks workedMonth,HashMap<UUID, Double> workHoursOfMonth,int year, int month){
        for(UUID id :workHoursOfMonth.keySet()){
            reportAssistant(workedMonth,id,workHoursOfMonth,year,month);
        }

    }
    public static ArrayList<MonthReport> reportOutput(AssistantMonthWorks workedMonth,HashMap<UUID, Double> workHoursOfMonth,int year, int month){
        ArrayList<MonthReport> output = new ArrayList<>();
        for(UUID id :workHoursOfMonth.keySet()){
            output.add(reportAssistant(workedMonth,id,workHoursOfMonth,year,month));
        }
        return output;
    }
    public static MonthReport reportAssistant(AssistantMonthWorks workedMonth, UUID assistantID, HashMap<UUID, Double> workHoursOfMonth,int year, int month){
            double workedTotal = 0D;
            double workedAtDayTotal = 0D;
            double workedAtNightTotal = 0D;
            double workFund = workHoursOfMonth.get(assistantID);

            MonthReport mon = new MonthReport(assistantID,workFund,workedTotal,workedAtDayTotal,workedAtNightTotal,year,month);
            HashMap<Integer, AssistantWorkShift> w = workedMonth.getFinishedWork().get(assistantID);
            int totaWorked = 0;

            for(Integer in :workedMonth.getFinishedWork().get(assistantID).keySet() ){
                AssistantWorkShift wok = w.get(in);
                ArrayList<WorkShiftSubInterval> workSubIn=  wok.getWorkedIntervals();
                workedTotal = workedTotal+ wok.getWorkedMinutes();
                double workedAtN = 0D;
                double workedAtD= 0D;
                double workedT= 0D;
                double workedAtWeekend =0D;
                for(WorkShiftSubInterval we : workSubIn){
                    double lengthOfNight = calculateNightHours(wok.isType(),we);
                    double totalWork = ChronoUnit.MINUTES.between(we.getStartOf(),we.getEndOf());
                    workedAtN += lengthOfNight;
                    workedAtNightTotal += lengthOfNight;
                    workedAtD += (totalWork-lengthOfNight);
                    workedT += totalWork;
                    workedAtDayTotal += totalWork;
                    workedAtWeekend+= calculateWeekend(we);

                }
                DayReport rep = new DayReport(workedT,workedAtD,workedAtN,workedAtWeekend,wok.getDay());
                mon.getDailyReports().add(rep);
                totaWorked += wok.getWorkedMinutes();

            }
    return mon;

    }
    private static double calculateWeekend(WorkShiftSubInterval workShift){
        LocalDateTime start = workShift.getStartOf();
        LocalDateTime end = workShift.getEndOf();
            // Ensure the interval is not longer than 12 hours
            if (ChronoUnit.HOURS.between(start, end) > 12) {
                throw new IllegalArgumentException("The interval cannot be longer than 12 hours.");
            }

            double weekendMinutes = 0;

            // Check if start day is a weekend
            if (isWeekend(start)) {
                // If the entire interval is within the same day
                if (start.toLocalDate().isEqual(end.toLocalDate())) {
                    weekendMinutes = ChronoUnit.MINUTES.between(start, end);
                } else {
                    // Calculate minutes from start to the end of the start day
                    LocalDateTime endOfDay = start.toLocalDate().atTime(23, 59);
                    weekendMinutes += ChronoUnit.MINUTES.between(start, endOfDay) + 1; // +1 for the last minute of the day
                }
            }

            // Check if end day is a weekend and not the same day as start
            if (isWeekend(end) && !start.toLocalDate().isEqual(end.toLocalDate())) {
                // Calculate minutes from the start of the end day to the end time
                LocalDateTime startOfEndDay = end.toLocalDate().atStartOfDay();
                weekendMinutes += ChronoUnit.MINUTES.between(startOfEndDay, end);
            }

            return weekendMinutes;
        }

        private static boolean isWeekend(LocalDateTime dateTime) {
            DayOfWeek day = dateTime.getDayOfWeek();
            return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;


    }

    private static  double calculateNightHours(boolean status, WorkShiftSubInterval workShift){
        LocalDateTime nightStart = null;
        LocalDateTime nightEnd = null;
        ArrayList<LocalDateTime> starts = new ArrayList<>();
        ArrayList<LocalDateTime> ends = new ArrayList<>();
        if(status){
            return standardInterval(nightStart,nightEnd,starts,ends,workShift);
        }else{
            if(workShift.getStartOf().getDayOfMonth()==workShift.getEndOf().getDayOfMonth()){
            if(workShift.getStartOf().getHour()<12){
                LocalDateTime dict = workShift.getStartOf();
                LocalDate startDate = LocalDate.of(dict.getYear(),dict.getMonth(), dict.getDayOfMonth()).plusDays(-1);
                nightStart = LocalDateTime.of(startDate, LocalTime.of(22,0));
                nightEnd = LocalDateTime.of(dict.getYear(),dict.getMonth(), dict.getDayOfMonth(), 6, 0);
                starts = new ArrayList<>(Arrays.asList(nightStart,workShift.getStartOf()));
                ends = new ArrayList<>(Arrays.asList(nightEnd,workShift.getEndOf()));
                LocalDateTime minDate = starts.stream().max(Comparator.naturalOrder()).orElse(null);
                LocalDateTime maxDate = ends.stream().min(Comparator.naturalOrder()).orElse(null);
                return ChronoUnit.MINUTES.between(minDate,maxDate);

            }else{
                return standardInterval(nightStart,nightEnd,starts,ends,workShift);
            }
            }else{
               return standardInterval(nightStart,nightEnd,starts,ends,workShift);
            }
        }

    }
    private static double standardInterval(LocalDateTime nightStart, LocalDateTime nightEnd,ArrayList<LocalDateTime> starts,ArrayList<LocalDateTime> ends,WorkShiftSubInterval workShift){
        LocalDateTime dict = workShift.getStartOf();
        nightStart = LocalDateTime.of(dict.getYear(),dict.getMonth(), dict.getDayOfMonth(), 22, 0);
        LocalDate endDate = LocalDate.of(dict.getYear(),dict.getMonth(), dict.getDayOfMonth()).plusDays(1);
        nightEnd = LocalDateTime.of(endDate, LocalTime.of(6,0));
        starts = new ArrayList<>(Arrays.asList(nightStart,workShift.getStartOf()));
        ends = new ArrayList<>(Arrays.asList(nightEnd,workShift.getEndOf()));
        LocalDateTime minDate = starts.stream().max(Comparator.naturalOrder()).orElse(null);
        LocalDateTime maxDate = ends.stream().min(Comparator.naturalOrder()).orElse(null);
        if(ChronoUnit.MINUTES.between(minDate,maxDate)>0){
            return ChronoUnit.MINUTES.between(minDate,maxDate);
        }else{
            return 0;
        }

    }
}
