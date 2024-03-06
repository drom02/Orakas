package graphics.sorter.Structs;

import graphics.sorter.ClientProfile;

import java.util.UUID;

public class LocationRepresentative {
    public ClientDay getClDay() {
        return clDay;
    }

    public void setClDay(ClientDay clDay) {
        this.clDay = clDay;
    }

    public ClientProfile getClp() {
        return clp;
    }

    public void setClp(ClientProfile clp) {
        this.clp = clp;
    }

    private ClientProfile clp;
    private ClientDay clDay;
@Override
public String toString(){
    if(clp==null){
        return null;
    }else{
        return clp.getName() +" "+ clp.getSurname();
    }


}}
