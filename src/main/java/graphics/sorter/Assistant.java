package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphics.sorter.AssistantAvailability.ShiftAvailability;

import java.util.ArrayList;
import java.util.UUID;

public class Assistant extends Human{
    //Typ pracovní smlouvy
    private String ContractType;
    //Hodnota pro contract type
    private double contractTime;
    //Zda preferuje pracovat přesčasy
    private boolean likesOvertime;
    //Seznam klientů a vztahu s nimi
    private ArrayList<ArrayList<UUID>> clientPreference;
    private ArrayList<ShiftAvailability> workDays;
    private boolean emergencyAssistant;
    private boolean isDriver;
    @JsonCreator
     public Assistant(@JsonProperty("ID" )UUID ID,@JsonProperty("status")boolean status,@JsonProperty("name")String name, @JsonProperty("surname")String surname,
                      @JsonProperty("contract")String contract, @JsonProperty("work")double work, @JsonProperty("overtime")boolean overtime,
                      @JsonProperty("comments")String comments, @JsonProperty("workDays")ArrayList<ShiftAvailability> workDays,
                      @JsonProperty("clientPreference") ArrayList<ArrayList<UUID>> clientpreference,@JsonProperty("emergencyAssistant")boolean emergencyAssistant,@JsonProperty("isDriver")boolean isDriver){
        setID(ID);
        setActivityStatus(status);
        setName(name);
        setSurname(surname);
        setContractType(contract);
        setContractTime(work);
        setLikesOvertime(overtime);
        setComment(comments);
        setClientPreference(clientpreference);
        setEmergencyAssistant(emergencyAssistant);
        setDriver(isDriver);
        if( (workDays == null)){
            setWorkDays(ShiftAvailability.generateWeek());
        }else{
            setWorkDays(workDays);
        }
    }
    public boolean isDriver() {
        return isDriver;
    }

    public void setDriver(boolean driver) {
        isDriver = driver;
    }
    public boolean isEmergencyAssistant() {
        return emergencyAssistant;
    }

    public void setEmergencyAssistant(boolean emergencyAssistant) {
        this.emergencyAssistant = emergencyAssistant;
    }

    public String getContractType() {
        return ContractType;
    }

    public void setContractType(String contractType) {
        ContractType = contractType;
    }
    public double getContractTime() {
        return contractTime;
    }

    public void setContractTime(double contractTime) {
        this.contractTime = contractTime;
    }

    public Boolean getLikesOvertime() {
        return likesOvertime;
    }

    public void setLikesOvertime(Boolean likesOvertime) {
        this.likesOvertime = likesOvertime;
    }

    public boolean isLikesOvertime() {
        return likesOvertime;
    }

    public void setLikesOvertime(boolean likesOvertime) {
        this.likesOvertime = likesOvertime;
    }

    public ArrayList<ShiftAvailability> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(ArrayList<ShiftAvailability> workDays) {
        this.workDays = workDays;
    }

    public ArrayList<ArrayList<UUID>> getClientPreference() {
        return clientPreference;
    }

    public void setClientPreference(ArrayList<ArrayList<UUID>> clientPreference) {
        this.clientPreference = clientPreference;
    }

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (!(o instanceof Assistant)) {
            return false;
        }
        Assistant a = (Assistant) o;
        if(this.getID().equals(a.getID())){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public int hashCode() {
        return getID().hashCode();
    }
}
