package Orakas.Structs;

import Orakas.Humans.ClientProfile;
import Orakas.Structs.TimeStructs.ClientDay;
import Orakas.Structs.TimeStructs.ServiceInterval;
/*
Class used for
 */
public class LocationRepresentative {

    public ClientProfile getClp() {
        return clp;
    }

    public void setClp(ClientProfile clp) {
        this.clp = clp;
    }

    private ClientProfile clp;

    public ServiceInterval getServiceInterval() {
        return serviceInterval;
    }

    public void setServiceInterval(ServiceInterval serviceInterval) {
        this.serviceInterval = serviceInterval;
    }

    private ServiceInterval serviceInterval;

    public ClientDay getCld() {
        return cld;
    }

    public void setCld(ClientDay cld) {
        this.cld = cld;
    }

    private ClientDay cld;
@Override
public String toString(){
    if(clp==null){
        return null;
    }else{
        return clp.getName() +" "+ clp.getSurname();
    }


}}
