package graphics.sorter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import graphics.sorter.Structs.AvailableAssistants;
import graphics.sorter.Structs.ClientMonth;
import graphics.sorter.Structs.ListOfClients;

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
         Client client = new Client("Client", "Clientov", new ClientMonth(Month.DECEMBER,2024));
         Client client1 = new Client("Client2", "Clientov2", new ClientMonth(Month.DECEMBER,2024));
         Client client2 = new Client("Client3", "Clientov3", new ClientMonth(Month.DECEMBER,2024));
         Client client3 = new Client("Client4", "Clientov3", new ClientMonth(Month.DECEMBER,2024));
         ListOfClients clLs = new ListOfClients();
         clLs.getClientList().add(client);
         clLs.getClientList().add(client1);
         clLs.getClientList().add(client2);
         clLs.getClientList().add(client3);
         saveClientInfo(clLs);

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
    public ListOfClients loadClientInfo() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        byte[]  jsonData = Files.readAllBytes(Paths.get("E:\\JsonWriteTest\\Clients.json"));
        ListOfClients listOfA = objectMapper.readValue(jsonData, ListOfClients.class );
        return listOfA;
    }
    public void saveClientInfo(ListOfClients lias) throws IOException {
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

}
