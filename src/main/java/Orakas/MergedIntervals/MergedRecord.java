package Orakas.MergedIntervals;

import Orakas.Structs.TimeStructs.ServiceInterval;

import java.util.ArrayList;
import java.util.UUID;

public class MergedRecord {



    private ArrayList<ServiceInterval> mergedIntervals = new ArrayList<>();
    private ServiceInterval primaryInterval;
    private boolean wasRead = false;
    private UUID location;

    public ArrayList<ServiceInterval> getMergedIntervals() {
        return mergedIntervals;
    }
    public boolean addMergedIntervals(ServiceInterval serviceInterval) {
        if(getPrimaryInterval()!=null){
            if(serviceInterval.serviceIntervalLength()> getPrimaryInterval().getIntervalLength()){
                setPrimaryInterval(serviceInterval);
            }
        }else{
            setPrimaryInterval(serviceInterval);
        }
        mergedIntervals.add(serviceInterval);
        return true;
    }
    public void setMergedIntervals(ArrayList<ServiceInterval> mergedIntervals) {
        this.mergedIntervals = mergedIntervals;
    }
    public boolean isWasRead() {
        return wasRead;
    }
    public void setWasRead(boolean wasRead) {
        this.wasRead = wasRead;
    }
    public UUID getLocation() {
        return location;
    }

    public void setLocation(UUID location) {
        this.location = location;
    }
    public ServiceInterval getPrimaryInterval() {
        return primaryInterval;
    }

    public void setPrimaryInterval(ServiceInterval primaryInterval) {
        this.primaryInterval = primaryInterval;
    }

}
