package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Assistant extends Human{
    //Typ pracovní smlouvy
    private String ContractType;
    //Hodnota pro contract type
    private double workTime;
    //Zda preferuje pracovat přesčasy
    private boolean likesOvertime;
    //Zda pracuje pouze denní směny
    private boolean worksOnlyDay;
    //Zda pracuje pouze noční směny
    private boolean worksOnlyNight;
    //Komentáře k danému assisentovi
    //Seznam klientů a vztahu s nimi
    private ArrayList<Client> clientList;


    private ArrayList<ArrayList<UUID>> clientPreference;

    private int[] workDays;
    @JsonCreator
     public Assistant(@JsonProperty("ID" )UUID ID,@JsonProperty("status")boolean status,@JsonProperty("name")String name, @JsonProperty("surname")String surname,
                      @JsonProperty("contract")String contract, @JsonProperty("work")double work, @JsonProperty("overtime")boolean overtime,
                      @JsonProperty("worksDay")boolean worksDay, @JsonProperty("worksNight")boolean worksNight, @JsonProperty("comments")String comments,
                      @JsonProperty("workDays")int[] workDays, @JsonProperty("clientPreference") ArrayList<ArrayList<UUID>> clientpreference){
        setID(ID);
        setActivityStatus(status);
        setName(name);
        setSurname(surname);
        setContractType(contract);
        setWorkTime(work);
        setLikesOvertime(overtime);
        setWorksOnlyDay(worksDay);
        setWorksOnlyNight(worksNight);
        setComment(comments);
        setClientPreference(clientpreference);


        if( (workDays == null)){
            setWorkDays(new int[]{1,1,1,1,1,0,0,1,1,1,1,1,0,0});
        }else{
            setWorkDays(workDays);
        }


    }
    public String getContractType() {
        return ContractType;
    }

    public void setContractType(String contractType) {
        ContractType = contractType;
    }
    public double getWorkTime() {
        return workTime;
    }

    public void setWorkTime(double workTime) {
        this.workTime = workTime;
    }

    public Boolean getLikesOvertime() {
        return likesOvertime;
    }

    public void setLikesOvertime(Boolean likesOvertime) {
        this.likesOvertime = likesOvertime;
    }

    public Boolean getWorksOnlyDay() {
        return worksOnlyDay;
    }

    public void setWorksOnlyDay(Boolean worksOnlyDay) {
        this.worksOnlyDay = worksOnlyDay;
    }

    public Boolean getWorksOnlyNight() {
        return worksOnlyNight;
    }

    public void setWorksOnlyNight(Boolean worksOnlyNight) {
        this.worksOnlyNight = worksOnlyNight;
    }
    @JsonIgnore
       public ArrayList<Client> getClientList() {
        return clientList;
    }

    public void setClientList(ArrayList<Client> clientList) {
        this.clientList = clientList;
    }


    public boolean isLikesOvertime() {
        return likesOvertime;
    }

    public void setLikesOvertime(boolean likesOvertime) {
        this.likesOvertime = likesOvertime;
    }

    public boolean isWorksOnlyDay() {
        return worksOnlyDay;
    }

    public void setWorksOnlyDay(boolean worksOnlyDay) {
        this.worksOnlyDay = worksOnlyDay;
    }

    public boolean isWorksOnlyNight() {
        return worksOnlyNight;
    }

    public void setWorksOnlyNight(boolean worksOnlyNight) {
        this.worksOnlyNight = worksOnlyNight;
    }

    public int[] getWorkDays() {
        return workDays;
    }

    public void setWorkDays(int[] workDays) {
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
        if(this.getName().equals(a.getName()) && this.getSurname().equals(a.getSurname())){
            return true;
        }else{
            return false;
        }
    }
}
