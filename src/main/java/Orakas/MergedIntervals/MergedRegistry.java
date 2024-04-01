package Orakas.MergedIntervals;

import Orakas.Structs.TimeStructs.ClientDay;
import Orakas.Structs.TimeStructs.ServiceInterval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MergedRegistry {
    public HashMap<Integer, HashMap<Boolean,HashMap<UUID, MergedRecord>>> getMergedRegistry() {
        return mergedRegistry;
    }
    public void setMergedRegistry(HashMap<Integer, HashMap<Boolean,HashMap<UUID, MergedRecord>>> mergedRegistry) {
        this.mergedRegistry = mergedRegistry;
    }
    private HashMap<Integer, HashMap<Boolean,HashMap<UUID, MergedRecord>>> mergedRegistry = new HashMap<>();
    public MergedRecord getMergedRecord(Integer i, UUID locationID, boolean dayState){
        return mergedRegistry.get(i).get(dayState).get(locationID);
    }
    public void addMergedRecord(Integer i, MergedRecord mergedRecord, UUID locationID,boolean dayState){
            mergedRegistry.get(i).get(dayState).put(locationID,mergedRecord);
    }
    public void addIntervalToMergedRecord(Integer i, ServiceInterval serviceInterval, UUID locationID, boolean dayState){
        if(mergedRegistry.get(i).get(dayState).get(locationID)!=null){
            mergedRegistry.get(i).get(dayState).get(locationID).addMergedIntervals(serviceInterval);
        }else{
            mergedRegistry.putIfAbsent(i,new HashMap<>());
            mergedRegistry.get(i).putIfAbsent(dayState,new HashMap<>());
            mergedRegistry.get(i).get(dayState).putIfAbsent(locationID,new MergedRecord());
            mergedRegistry.get(i).get(dayState).get(locationID).addMergedIntervals(serviceInterval);
        }

    }
    public MergedRecord checkExistence(ClientDay cl, ServiceInterval serviceInterval) {
        HashMap<Boolean, HashMap<UUID, MergedRecord>> dayMap = mergedRegistry.get(cl.getDay());
        if (dayMap != null) {
            HashMap<UUID, MergedRecord> statusMap = dayMap.get(cl.getDayStatus());
            if (statusMap != null) {

                return statusMap.get(serviceInterval.getLocation());
            }
        }
        return null;
    }
}
