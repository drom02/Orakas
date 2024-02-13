package graphics.sorter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import graphics.sorter.Structs.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class JsonManip {
    private String path;
    public void initialize() throws IOException {
        Settings set = loadSettings("E:\\JsonWriteTest\\");
        path = set.getFilePath();
        ArrayList<String> directories = new ArrayList<>(Arrays.asList("Assistants","Clients","Settings","Locations","ClientRequirements","AvailableAssistants"));
        for(String st : directories){
           Files.createDirectories(Paths.get(path + st));
        }
    }
    public JsonManip(){
        try {
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
         */
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
             listOfClientMonths.getListOfClientMonths().add(new ClientMonth(Month.of(loadSettings(path + "").getCurrentMonth()),loadSettings(path + "").getCurrentYear(),cl.getID(), null));

         }
         saveClientRequirementsForMonth(listOfClientMonths, loadSettings(path + ""));
        //saveLocations(new ListOfLocations(), loadSettings(path + "").getFilePath());
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
    public ListOfClients loadClientInfo(Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get(path+"Clients\\Clients.json"));
        ListOfClientsProfiles listOfA = objectMapper.readValue(jsonData, ListOfClientsProfiles.class );
        byte[]  jsonDataMonth = Files.readAllBytes(Paths.get(set.getFilePath()+  "ClientRequirements\\ClientRequirements." +set.getCurrentMonth()+"." +set.getCurrentYear()+ ".json"));
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
            generateNewMonthsAssistants(loadSettings(path + ""));
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
    public Settings loadSettings(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\Settings\\Settings"+ ".json"));
        Settings set = objectMapper.readValue(jsonData, Settings.class );
        return set;
    }
    public void saveSettings(Settings lias, String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(path+"Settings\\Settings"+".json"),lias);
    }
    public void generateEmptyState(Settings settings) throws IOException {
        JsonManip jsom = new JsonManip();
        AvailableAssistants availableAssistants = new AvailableAssistants();
        ArrayList<ArrayList<Assistant>> dayList = new ArrayList<>();
        ArrayList<ArrayList<Assistant>> nightList = new ArrayList<>();
        int shift = 0;

        for(int i = 0; i < Month.of(settings.getCurrentMonth()).length(Year.isLeap(settings.getCurrentYear())); i++){
            dayList.add(new ArrayList<>());
            nightList.add(new ArrayList<>());

        }

        availableAssistants.setAvailableAssistantsAtDays(dayList);
        availableAssistants.setAvailableAssistantsAtNights(nightList);
        jsom.saveAvailableAssistantInfo(availableAssistants,settings);
    }
    public void generateNewMonthsAssistants(Settings settings) throws IOException {
        JsonManip jsom = new JsonManip();
        ListOfAssistants listOfA = jsom.loadAssistantInfo();
        AvailableAssistants availableAssistants = new AvailableAssistants();
        ArrayList<ArrayList<Assistant>> dayList = new ArrayList<>();
        ArrayList<ArrayList<Assistant>> nightList = new ArrayList<>();
        for(int i =0; i < Month.of(settings.getCurrentMonth()).length(Year.isLeap(settings.getCurrentYear())); i++){
            ArrayList<Assistant> inputDayList = new ArrayList<>();
            ArrayList<Assistant> inputNightList = new ArrayList<>();
            dayList.add(inputDayList);
            nightList.add(inputNightList);
            for(Assistant asis : listOfA.getAssistantList()){
                if(asis.getWorkDays()[(LocalDate.of(settings.getCurrentYear(), settings.getCurrentMonth(), i+1).getDayOfWeek().getValue()-1)] == 1 ){
                    inputDayList.add(asis);
                }
                if(asis.getWorkDays()[(LocalDate.of(settings.getCurrentYear(), settings.getCurrentMonth(), i+1).getDayOfWeek().getValue()+6)] == 1 ){
                    inputNightList.add(asis);
                }

            }


        }

        availableAssistants.setAvailableAssistantsAtDays(dayList);
        availableAssistants.setAvailableAssistantsAtNights(nightList);
        jsom.saveAvailableAssistantInfo(availableAssistants,settings);
    }
    public void  saveLocations(ListOfLocations lOL, String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(path+"Locations\\Locations"+".json"),lOL);

    }
    public ListOfLocations loadLocations(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get(path+"Locations\\Locations"+ ".json"));
        ListOfLocations lot = objectMapper.readValue(jsonData, ListOfLocations.class );
        return lot;
    }
}
