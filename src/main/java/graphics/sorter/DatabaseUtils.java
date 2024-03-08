package graphics.sorter;

import graphics.sorter.Structs.ClientDay;
import graphics.sorter.Structs.ClientMonth;
import graphics.sorter.Structs.ServiceInterval;
import graphics.sorter.Structs.ServiceIntervalArrayList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseUtils {
    public static int[] stringToIntArray(String st){
        String[] processed = st.split(",");
        return  Arrays.stream(processed).mapToInt(Integer::parseInt).toArray();
    }
    public static String IntArrayToString(int[] input){
        StringBuilder output = new StringBuilder(new String());
        for(int i : input){
            output.append(String.valueOf(i));
            output.append(",");
        }
        return output.toString();
    }
    public static String prepCDID(ClientDay input){
        StringBuilder output = new StringBuilder();
        output.append(input.getYear());
        output.append("-");
        output.append(input.getMonth().getValue());
        output.append("-");
        output.append(input.getDay());
        output.append("-");
        output.append(input.getDayStatus());
        return  output.toString();
    }
    public static String prepMID(ClientMonth mon){
        StringBuilder output = new StringBuilder();
        output.append(mon.getYear());
        output.append("-");
        output.append(mon.getMon().getValue());
        return  output.toString();
    }
    /*
      public static String prepSIID(ServiceIntervalArrayList serv){
        int i = 0;
        StringBuilder output = new StringBuilder();
        for(ServiceInterval se : serv){
            prepCDID(se.get)
        }

    }
     */

}
