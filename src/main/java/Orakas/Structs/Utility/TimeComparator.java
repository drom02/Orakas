package Orakas.Structs.Utility;

import java.time.LocalDateTime;

public class TimeComparator {

    public static boolean beforeOrE(LocalDateTime localDateTime1, LocalDateTime localDateTime2){
        if(localDateTime1.isBefore(localDateTime2)  || localDateTime1.equals(localDateTime2)){
            return true;
        }
        return false;
    }
    public static boolean afterOrE(LocalDateTime localDateTime1, LocalDateTime localDateTime2){
        if(localDateTime1.isAfter(localDateTime2)  || localDateTime1.equals(localDateTime2)){
            return true;
        }
        return false;
    }


}
