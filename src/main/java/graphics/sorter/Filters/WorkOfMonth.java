package graphics.sorter.Filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.UUID;

public class WorkOfMonth {


    private HashMap<UUID, Double>  resultOfMonth;
@JsonCreator
    public WorkOfMonth(HashMap<UUID, Double> input){
       setResultOfMonth(input);
    }
    @JsonIgnore
    public WorkOfMonth(){
        setResultOfMonth(new HashMap<>());
    }

    public HashMap<UUID, Double> getResultOfMonth() {
        return resultOfMonth;
    }

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
