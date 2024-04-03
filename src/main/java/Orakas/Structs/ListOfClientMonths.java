package Orakas.Structs;

import Orakas.Structs.TimeStructs.ClientMonth;

import java.util.ArrayList;
import java.util.UUID;

public class ListOfClientMonths {
    public ArrayList<ClientMonth> getListOfClientMonths() {
        return listOfClientMonths;
    }

    public void setListOfClientMonths(ArrayList<ClientMonth> listOfClientMonths) {
        this.listOfClientMonths = listOfClientMonths;
    }

    private ArrayList<ClientMonth> listOfClientMonths = new ArrayList<ClientMonth>();
    public ClientMonth getMonthOfSpecificClient(UUID id){
        if(!getListOfClientMonths().isEmpty()){
            for( ClientMonth iter : getListOfClientMonths()){
                if(iter.getClientId().equals(id)){
                    return  iter;
                }
            }
        }else{
            return null;
        }
       return null;
    }

}
