package Orakas;

import Orakas.AssistantAvailability.AssistantAvailability;
import Orakas.AssistantAvailability.Availability;
import Orakas.AssistantAvailability.ShiftAvailability;
import Orakas.Database.Database;
import Orakas.Humans.Assistant;
import Orakas.Humans.Client;
import Orakas.Structs.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import Orakas.Structs.Availability.AvailableAssistants;
import Orakas.Structs.TimeStructs.ClientMonth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class JsonManip {
    private static JsonManip jsonManip;

    private String path;
    public void initialize() throws IOException {
        Settings set = Settings.getSettings();
        path = set.getFilePath();
        ArrayList<String> directories = new ArrayList<>(Arrays.asList("Assistants","Clients","Settings","Locations","ClientRequirements","AvailableAssistants"));
        for(String st : directories){
           Files.createDirectories(Paths.get(path + st));
        }
    }
    private JsonManip(){
        try {
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static JsonManip getJsonManip(){
        if(jsonManip==null){
            return new JsonManip();
        }else{
            return jsonManip;
        }
    }
    public void saveAssistantInfo(ListOfAssistants lias) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(path +"Assistants\\Assistants.json"),lias);
    }



    public void saveClientInfo(ListOfClientsProfiles lias) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(path +"Clients\\Clients.json"),lias);
    }





    public static String loadRedirect() {
        new File(System.getenv("APPDATA")+"\\ORAKAS").mkdir();
        new File(System.getenv("APPDATA")+"\\ORAKAS\\Data").mkdir();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try{byte[]jsonData = Files.readAllBytes(Paths.get(System.getenv("APPDATA")+"\\ORAKAS\\Data\\redirect.json"));
            RedirectPath  set = objectMapper.readValue(jsonData, RedirectPath .class );
            return set.getPath();
        }
        catch (Exception e){
            try {
                return saveRedirect(null).getPath();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
    public static RedirectPath saveRedirect(String lias) throws IOException {
        String st;
        if(lias == null){
             st =  System.getenv("APPDATA")+"\\ORAKAS\\Data\\";
        }else{
            st = lias;
        }
        RedirectPath l = new RedirectPath();
        l.setPath(st);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(System.getenv("APPDATA")+"\\ORAKAS\\redirect.json"),lias);
        return l;
    }

    public static void generateEmptyState(Settings settings) throws IOException {
        AvailableAssistants availableAssistants = new AvailableAssistants(settings.getCurrentYear(),settings.getCurrentMonth());
        ArrayList<ArrayList<AssistantAvailability>> dayList = new ArrayList<>();
        ArrayList<ArrayList<AssistantAvailability>> nightList = new ArrayList<>();
        int shift = 0;
        for(int i = 0; i < Month.of(settings.getCurrentMonth()).length(Year.isLeap(settings.getCurrentYear())); i++){
            dayList.add(new ArrayList<>());
            nightList.add(new ArrayList<>());

        }

        availableAssistants.setAvailableAssistantsAtDays(dayList);
        availableAssistants.setAvailableAssistantsAtNights(nightList);
        Database.saveAssistantAvailability(settings.getCurrentYear(),settings.getCurrentMonth(),availableAssistants);
    }
    public void  generateNewMonthsAssistants(int year,int month) throws IOException {
        //JsonManip jsom = new JsonManip();
        ListOfAssistants listOfA = Database.loadAssistants();
        AvailableAssistants availableAssistants = new AvailableAssistants(year,month);
        ArrayList<ArrayList<AssistantAvailability>> dayList = new ArrayList<>(31);
        ArrayList<ArrayList<AssistantAvailability>> nightList = new ArrayList<>(31);
        for(int i =0; i < Month.of(month).length(Year.isLeap(year)); i++){
            ArrayList<AssistantAvailability> inputDayList = new ArrayList<>();
            ArrayList<AssistantAvailability> inputNightList = new ArrayList<>();
            dayList.add(inputDayList);
            nightList.add(inputNightList);
            for(Assistant asis : listOfA.getAssistantList()){
                ArrayList<HashMap<DayOfWeek, Availability>> workDayHashMap =  daysOfWeekIndex(asis);
                LocalDate locDate = LocalDate.of(year,month,i+1);
                inputDayList.add(new AssistantAvailability(asis.getID(),workDayHashMap.get(0).get(locDate.getDayOfWeek())));
                inputNightList.add(new AssistantAvailability(asis.getID(),workDayHashMap.get(1).get(locDate.getDayOfWeek())));
                /*
                if(asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue()).getState()){
                    inputDayList.add(new AssistantAvailability(asis.getID(),asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue()).getAvailability()));
                }
                if(asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue() + 6).getState()){
                    inputNightList.add(new AssistantAvailability(asis.getID(),asis.getWorkDays().get(LocalDate.of(year, month, i + 1).getDayOfWeek().getValue()+ 6).getAvailability()));
                }
                 */

            }
        }
        availableAssistants.setAvailableAssistantsAtDays(dayList);
        availableAssistants.setAvailableAssistantsAtNights(nightList);
        Database.saveAssistantAvailability(year, month, availableAssistants);
    }
    private ArrayList<HashMap<DayOfWeek, Availability>> daysOfWeekIndex(Assistant asis){
        ArrayList<HashMap<DayOfWeek, Availability>> outputHash = new ArrayList<>();
        int dayI = 0;
        for(int i =0; i<2;i++){
            outputHash.add(new HashMap<DayOfWeek, Availability>());
            while(dayI<(7+i*7)){
                ShiftAvailability sh = asis.getWorkDays().get(dayI);
                outputHash.get(i).put(sh.getDay(),sh.getAvailability());
                dayI++;
            }
        }
        return outputHash;
    }
    public void  saveLocations(ListOfLocations lOL) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(path+"Locations\\Locations"+".json"),lOL);

    }
}
