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
     public void jsonTest() throws IOException {
       // byte[] jsonData = Files.readAllBytes(Paths.get("employee.txt"));
         /*
         Assistant ass = new Assistant(UUID.randomUUID(),"Matěj", "Matějov", "Dpp", 0, false,false,false, "",null);
         Assistant ass1 = new Assistant(UUID.randomUUID(),"Jan", "Janov", "Dpp", 75, true,false,true, "Tralalal",null);
         Assistant ass2 = new Assistant(UUID.randomUUID(),"Honza", "Honzov", "Dpp", 75, true,false,true, "Tralalal",null);
         Assistant ass3 = new Assistant(UUID.randomUUID(),"Milan", "Milanov", "Dpp", 75, true,false,true, "Tralalal",null);
         Assistant ass4 = new Assistant(UUID.randomUUID(),"Martin", "Martinov", "Dpp", 75, true,false,true, "Tralalal",null);
         Assistant ass5 = new Assistant(UUID.randomUUID(),"Test", "Testov", "Dpp", 75, true,false,true, "Tralalal",null);
         Assistant ass6 = new Assistant(UUID.randomUUID(),"Jan", "Testov", "Dpp", 75, true,false,true, "Tralalal",null);
        ListOfAssistants lias = new ListOfAssistants(new ArrayList<Assistant>());
        lias.assistantList.add(ass);
        lias.assistantList.add(ass1);
         lias.assistantList.add(ass2);
         lias.assistantList.add(ass3);
         lias.assistantList.add(ass4);
         lias.assistantList.add(ass5);
         lias.assistantList.add(ass6);
        saveAssistantInfo(lias);
          */

        /*
         Client client = new Client("Client", "Clientov", new ClientMonth(Month.DECEMBER,2024));
         Client client1 = new Client("Client2", "Clientov2", new ClientMonth(Month.DECEMBER,2024));
         Client client2 = new Client("Client3", "Clientov3", new ClientMonth(Month.DECEMBER,2024));
         Client client3 = new Client("Client4", "Clientov3", new ClientMonth(Month.DECEMBER,2024));

         ClientProfile client = new ClientProfile(UUID.randomUUID(),"Client", "Clientov", null);
         ClientProfile client1 = new ClientProfile(UUID.randomUUID(),"Client2", "Clientov2", null);
         ClientProfile client2 = new ClientProfile(UUID.randomUUID(),"Client3", "Clientov3", null);
         ClientProfile client3 = new ClientProfile(UUID.randomUUID(),"Client4", "Clientov4", null);
         ListOfClientsProfiles clLs = new ListOfClientsProfiles();
         clLs.getClientList().add(client);
         clLs.getClientList().add(client1);
         clLs.getClientList().add(client2);
         clLs.getClientList().add(client3);
         saveClientInfo(clLs);
         ListOfClientMonths listOfClientMonths = new ListOfClientMonths();
         for (ClientProfile cl : clLs.getClientList()){
             listOfClientMonths.getListOfClientMonths().add(new ClientMonth(Month.of(loadSettings().getCurrentMonth()),loadSettings().getCurrentYear(),cl.getID(), null));

         }
         saveClientRequirementsForMonth(listOfClientMonths, loadSettings());
        //saveLocations(new ListOfLocations(), loadSettings(path + "").getFilePath());

         */
    }
    public void saveAssistantInfo(ListOfAssistants lias) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(path +"Assistants\\Assistants.json"),lias);
    }
    public ListOfAssistants loadAssistantInfo() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get(path+"Assistants\\Assistants.json"));
        ListOfAssistants listOfA = objectMapper.readValue(jsonData, ListOfAssistants.class );
        return listOfA;
    }
    public ListOfClients loadClientInfo(Settings set)  {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
        byte[]  jsonData = Files.readAllBytes(Paths.get(path+"Clients\\Clients.json"));
        ListOfClientsProfiles listOfA = objectMapper.readValue(jsonData, ListOfClientsProfiles.class );
        byte[]  jsonDataMonth = new byte[0];
        jsonDataMonth = Files.readAllBytes(Paths.get(set.getFilePath()+  "ClientRequirements\\ClientRequirements." +set.getCurrentMonth()+"." +set.getCurrentYear()+ ".json"));
            ListOfClientMonths listOfClm = objectMapper.readValue(jsonDataMonth, ListOfClientMonths.class );
            ListOfClients listOfClients = new ListOfClients();
            for(ClientProfile clP: listOfA.getClientList()){
                Client out;
                if(!(listOfClm.getMonthOfSpecificClient(clP.getID()) == null)){
                    out = clP.convertToClient(listOfClm.getMonthOfSpecificClient(clP.getID()));
                }else{
                    out = clP.convertToClient(new ClientMonth(Month.of(set.getCurrentMonth()), set.getCurrentYear(), clP.getID(), clP.getHomeLocation()));
                }
                listOfClients.getClientList().add(out);
            }
            return listOfClients;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public ListOfClientsProfiles loadClientProfileInfo() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get(path +"Clients\\Clients.json"));
        ListOfClientsProfiles listOfA = objectMapper.readValue(jsonData, ListOfClientsProfiles.class );
        return listOfA;
    }
    public void saveClientInfo(ListOfClientsProfiles lias) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(path +"Clients\\Clients.json"),lias);
    }
    public void saveAvailableAssistantInfo(AvailableAssistants lias, Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(path +  "AvailableAssistants\\AvailableAssistants."+ set.getCurrentMonth() +"."+set.getCurrentYear() +".json"),lias);
    }
    public AvailableAssistants loadAvailableAssistantInfo(Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = new byte[0];
        try {
            jsonData = Files.readAllBytes(Paths.get(path +  "AvailableAssistants\\AvailableAssistants."+ set.getCurrentMonth() +"."+set.getCurrentYear() +".json"));
        } catch (IOException e) {
            Settings setTemp = Settings.getSettings();
            generateNewMonthsAssistants(setTemp.getCurrentYear(), setTemp.getCurrentMonth());
            jsonData = Files.readAllBytes(Paths.get(path +  "AvailableAssistants\\AvailableAssistants."+ set.getCurrentMonth() +"."+set.getCurrentYear() +".json"));
        }
        AvailableAssistants listOfA = objectMapper.readValue(jsonData, AvailableAssistants.class );

        return listOfA;
    }
    public void saveClientRequirementsForMonth(ListOfClientMonths lias, Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(set.getFilePath()+  "ClientRequirements\\ClientRequirements." +set.getCurrentMonth()+"." +set.getCurrentYear()+ ".json"),lias);

    }
    public ListOfClientMonths loadClientRequirementsForMonth( Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get(set.getFilePath()+  "ClientRequirements\\ClientRequirements." +set.getCurrentMonth()+"." +set.getCurrentYear()+ ".json"));
        ListOfClientMonths listOfClm = objectMapper.readValue(jsonData, ListOfClientMonths.class );
        return listOfClm;
    }
    public static Settings loadSettings() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get(".\\Settings.json"));
        Settings set = objectMapper.readValue(jsonData, Settings.class );
        return set;
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
    public static void saveSettings(Settings lias) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(".\\Settings.json"),lias);
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
    public ListOfLocations loadLocations(Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get(set.getFilePath()+"Locations\\Locations"+ ".json"));
        ListOfLocations lot = objectMapper.readValue(jsonData, ListOfLocations.class );
        return lot;
    }
}
