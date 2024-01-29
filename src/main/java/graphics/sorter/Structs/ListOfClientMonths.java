package graphics.sorter.Structs;

import java.util.ArrayList;

public class ListOfClientMonths {
    public ArrayList<ClientMonth> getListOfClientMonths() {
        return listOfClientMonths;
    }

    public void setListOfClientMonths(ArrayList<ClientMonth> listOfClientMonths) {
        this.listOfClientMonths = listOfClientMonths;
    }

    private ArrayList<ClientMonth> listOfClientMonths = new ArrayList<ClientMonth>();
    public ClientMonth getMonthOfSpecificClient(String id){
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
