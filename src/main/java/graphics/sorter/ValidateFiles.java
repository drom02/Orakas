package graphics.sorter;

import graphics.sorter.Structs.AvailableAssistants;
import graphics.sorter.Structs.ListOfClientsProfiles;
import graphics.sorter.Structs.ListOfLocations;
import graphics.sorter.Structs.Saveable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ValidateFiles {

    Settings set;
    {
        try {
            set = JsonManip.loadSettings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void run(){
        JsonManip jsom = new JsonManip();
        String[] st = {"Assistants\\Assistants.json","Locations\\Locations.json","Clients\\Clients.json","AvailableAssistants"};
        ArrayList<Saveable> save = new ArrayList<>(Arrays.asList(new ListOfAssistants(null), new ListOfLocations(), new ListOfClientsProfiles(),new AvailableAssistants()));
        int i = 0;
        for(String s:st){
            try {
                validate(s, save.get(i), jsom);
                System.out.println(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
    }
    private void validate(String name, Saveable type, JsonManip map) throws IOException {
        File fil = new File(set.getFilePath()+name);
        //System.out.println(set.getFilePath()+name);
        if(!fil.exists()){
            type.createNew(map);
            System.out.println("tip");
        }
    }
}
