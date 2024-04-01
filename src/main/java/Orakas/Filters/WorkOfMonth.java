package Orakas.Filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.UUID;


public class WorkOfMonth {

    private HashMap<UUID, Double>  resultOfMonth;
    @JsonCreator
    public WorkOfMonth(){
    }
    @JsonIgnore
    public WorkOfMonth(HashMap<UUID, Double> input){
       setResultOfMonth(input);
    }


    public HashMap<UUID, Double> getResultOfMonth() {
        return resultOfMonth;
    }
    @JsonProperty("resultOfMonth")
    public void setResultOfMonth(HashMap<UUID, Double> resultOfMonth) {
        this.resultOfMonth = resultOfMonth;
    }
    public void registerResult(UUID id, Double value){
        resultOfMonth.put(id,value);
    }
    public Double retrieveValue(UUID id){
    return resultOfMonth.get(id);

    }
}
