package graphics.sorter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import graphics.sorter.Structs.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.UUID;

public class JsonManip {

     public void jsonTest() throws IOException {
       // byte[] jsonData = Files.readAllBytes(Paths.get("employee.txt"));
        Assistant ass = new Assistant("Matěj", "Matějov", "Dpp", 0, false,false,false, "");
         Assistant ass1 = new Assistant("Jan", "Janov", "Dpp", 75, true,false,true, "Tralalal");
         Assistant ass2 = new Assistant("Honza", "Honzov", "Dpp", 75, true,false,true, "Tralalal");
         Assistant ass3 = new Assistant("Milan", "Milanov", "Dpp", 75, true,false,true, "Tralalal");
         Assistant ass4 = new Assistant("Martin", "Martinov", "Dpp", 75, true,false,true, "Tralalal");
         Assistant ass5 = new Assistant("Test", "Testov", "Dpp", 75, true,false,true, "Tralalal");
         Assistant ass6 = new Assistant("Jan", "Testov", "Dpp", 75, true,false,true, "Tralalal");
        ListOfAssistants lias = new ListOfAssistants(new ArrayList<Assistant>());
        lias.assistantList.add(ass);
        lias.assistantList.add(ass1);
         lias.assistantList.add(ass2);
         lias.assistantList.add(ass3);
         lias.assistantList.add(ass4);
         lias.assistantList.add(ass5);
         lias.assistantList.add(ass6);
        saveAssistantInfo(lias);
        /*
         Client client = new Client("Client", "Clientov", new ClientMonth(Month.DECEMBER,2024));
         Client client1 = new Client("Client2", "Clientov2", new ClientMonth(Month.DECEMBER,2024));
         Client client2 = new Client("Client3", "Clientov3", new ClientMonth(Month.DECEMBER,2024));
         Client client3 = new Client("Client4", "Clientov3", new ClientMonth(Month.DECEMBER,2024));
         */
         ClientProfile client = new ClientProfile(UUID.randomUUID(),"Client", "Clientov");
         ClientProfile client1 = new ClientProfile(UUID.randomUUID(),"Client2", "Clientov2");
         ClientProfile client2 = new ClientProfile(UUID.randomUUID(),"Client3", "Clientov3");
         ClientProfile client3 = new ClientProfile(UUID.randomUUID(),"Client4", "Clientov4");
         ListOfClientsProfiles clLs = new ListOfClientsProfiles();
         clLs.getClientList().add(client);
         clLs.getClientList().add(client1);
         clLs.getClientList().add(client2);
         clLs.getClientList().add(client3);
         saveClientInfo(clLs);
         ListOfClientMonths listOfClientMonths = new ListOfClientMonths();
         for (ClientProfile cl : clLs.getClientList()){
             listOfClientMonths.getListOfClientMonths().add(new ClientMonth(Month.of(loadSettings("E:\\JsonWriteTest\\").getCurrentMonth()),loadSettings("E:\\JsonWriteTest\\").getCurrentYear(),cl.getID()));

         }
         saveClientRequirementsForMonth(listOfClientMonths, loadSettings("E:\\JsonWriteTest\\"));

    }
    public void saveAssistantInfo(ListOfAssistants lias) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File("E:\\JsonWriteTest\\Assistants.json"),lias);
    }
    public ListOfAssistants loadAssistantInfo() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\Assistants.json"));
        ListOfAssistants listOfA = objectMapper.readValue(jsonData, ListOfAssistants.class );
        return listOfA;
    }
    public ListOfClients loadClientInfo(Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\Clients.json"));
        ListOfClientsProfiles listOfA = objectMapper.readValue(jsonData, ListOfClientsProfiles.class );
        byte[]  jsonDataMonth = Files.readAllBytes(Paths.get(set.getFilePath()+ "ClientRequirements." +set.getCurrentMonth()+"." +set.getCurrentYear()+ ".json"));
        ListOfClientMonths listOfClm = objectMapper.readValue(jsonDataMonth, ListOfClientMonths.class );
        ListOfClients listOfClients = new ListOfClients();
        for(ClientProfile clP: listOfA.getClientList()){
            Client out;
            if(!(listOfClm.getMonthOfSpecificClient(clP.getID()) == null)){
                out = clP.convertToClient(listOfClm.getMonthOfSpecificClient(clP.getID()));
            }else{
                out = clP.convertToClient(new ClientMonth(Month.of(set.getCurrentMonth()), set.getCurrentYear(), clP.getID()));
            }
            listOfClients.getClientList().add(out);
        }

        return listOfClients;
    }
    public ListOfClientsProfiles loadClientProfileInfo() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\Clients.json"));
        ListOfClientsProfiles listOfA = objectMapper.readValue(jsonData, ListOfClientsProfiles.class );
        return listOfA;
    }
    public void saveClientInfo(ListOfClientsProfiles lias) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File("E:\\JsonWriteTest\\Clients.json"),lias);
    }
    public void saveAvailableAssistantInfo(AvailableAssistants lias, Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File("E:\\JsonWriteTest\\AvailableAssistants."+ set.getCurrentMonth() +"."+set.getCurrentYear() +".json"),lias);
    }
    public AvailableAssistants loadAvailableAssistantInfo(Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = new byte[0];
        try {
            jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\AvailableAssistants."+ set.getCurrentMonth() +"."+set.getCurrentYear() +".json"));
        } catch (IOException e) {
            generateEmptyState(loadSettings("E:\\JsonWriteTest\\"));
            jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\AvailableAssistants."+ set.getCurrentMonth() +"."+set.getCurrentYear() +".json"));
        }
        AvailableAssistants listOfA = objectMapper.readValue(jsonData, AvailableAssistants.class );

        return listOfA;
    }
    public void saveClientRequirementsForMonth(ListOfClientMonths lias, Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(set.getFilePath()+ "ClientRequirements." +set.getCurrentMonth()+"." +set.getCurrentYear()+ ".json"),lias);
    }
    public ListOfClientMonths loadClientRequirementsForMonth( Settings set) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get(set.getFilePath()+ "ClientRequirements." +set.getCurrentMonth()+"." +set.getCurrentYear()+ ".json"));
        ListOfClientMonths listOfClm = objectMapper.readValue(jsonData, ListOfClientMonths.class );
        return listOfClm;
    }
    public Settings loadSettings(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get(path+"Settings"+ ".json"));
        Settings set = objectMapper.readValue(jsonData, Settings.class );
        return set;
    }
    public void saveSettings(Settings lias, String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File(path+"Settings"+".json"),lias);
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
}
