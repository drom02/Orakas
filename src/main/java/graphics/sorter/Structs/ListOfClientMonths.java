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
}
