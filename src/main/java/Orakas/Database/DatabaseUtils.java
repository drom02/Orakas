package Orakas.Database;

import Orakas.Structs.TimeStructs.ClientDay;
import Orakas.Structs.TimeStructs.ClientMonth;

import java.util.Arrays;
import java.util.UUID;
/*
Database containing tools used in database manipulation.
 */
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
        output.append(input.getClient());
        output.append("-");
        output.append(input.getDayStatus());
        return  output.toString();
    }
    public static String prepMID(ClientMonth mon){
        StringBuilder output = new StringBuilder();
        output.append(mon.getYear());
        output.append("-");
        output.append(mon.getMon().getValue());
        output.append("-");
        output.append(mon.getClientId().toString());
        return  output.toString();
    }
    public static String prepMID(int year, int month, UUID clientID){
        StringBuilder output = new StringBuilder();
        output.append(year);
        output.append("-");
        output.append(month);
        output.append("-");
        output.append(clientID.toString());
        return  output.toString();
    }
}
