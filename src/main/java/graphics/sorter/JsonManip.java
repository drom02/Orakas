package graphics.sorter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import graphics.sorter.Structs.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

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
         ClientProfile client = new ClientProfile("Client", "Clientov");
         ClientProfile client1 = new ClientProfile("Client2", "Clientov2");
         ClientProfile client2 = new ClientProfile("Client3", "Clientov3");
         ClientProfile client3 = new ClientProfile("Client4", "Clientov3");
         ListOfClientsProfiles clLs = new ListOfClientsProfiles();
         clLs.getClientList().add(client);
         clLs.getClientList().add(client1);
         clLs.getClientList().add(client2);
         clLs.getClientList().add(client3);
         saveClientInfo(clLs);
         ListOfClientMonths listOfClientMonths = new ListOfClientMonths();
         for (ClientProfile cl : clLs.getClientList()){
             listOfClientMonths.getListOfClientMonths().add(new ClientMonth(Month.of(12),2024,cl.getSurname()));

         }
         saveClientRequirementsForMonth(listOfClientMonths,12,2024);

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
    public ListOfClients loadClientInfo(int month, int year) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\Clients.json"));
        ListOfClientsProfiles listOfA = objectMapper.readValue(jsonData, ListOfClientsProfiles.class );
        byte[]  jsonDataMonth = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\"+month+"."+year+ ".json"));
        ListOfClientMonths listOfClm = objectMapper.readValue(jsonDataMonth, ListOfClientMonths.class );
        ListOfClients listOfClients = new ListOfClients();
        for(ClientProfile clP: listOfA.getClientList()){
            Client out;
            if(!(listOfClm.getMonthOfSpecificClient(clP.getClientId()) == null)){
                out = clP.convertToClient(listOfClm.getMonthOfSpecificClient(clP.getClientId()));
            }else{
                out = clP.convertToClient(new ClientMonth(Month.of(month), year, clP.getClientId()));
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
    public void saveAvailableAssistantInfo(AvailableAssistants lias) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File("E:\\JsonWriteTest\\AvailableAssistants.json"),lias);
    }
    public AvailableAssistants loadAvailableAssistantInfo() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\AvailableAssistants.json"));
        AvailableAssistants listOfA = objectMapper.readValue(jsonData, AvailableAssistants.class );
        return listOfA;
    }
    public void saveClientRequirementsForMonth(ListOfClientMonths lias, int month, int year) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(new File("E:\\JsonWriteTest\\" +month+"." +year+ ".json"),lias);
    }
    public ListOfClientMonths loadClientRequirementsForMonth(int month, int year) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\"+month+"."+year+ ".json"));
        ListOfClientMonths listOfClm = objectMapper.readValue(jsonData, ListOfClientMonths.class );
        return listOfClm;
    }

}
