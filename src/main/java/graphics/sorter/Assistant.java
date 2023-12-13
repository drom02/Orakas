package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Assistant extends Human{
    //Typ pracovní smlouvy
    private String ContractType;
    //Počet odpracovaných hodin za daný měsíc
    private double workTime;
    //Zda preferuje pracovat přesčasy
    private boolean likesOvertime;
    //Zda pracuje pouze denní směny
    private boolean worksOnlyDay;
    //Zda pracuje pouze noční směny
    private boolean worksOnlyNight;
    //Komentáře k danému assisentovi
    private String comments;
    //Seznam klientů a vztahu s nimi
    private ArrayList<Client> clientList;
    @JsonCreator
     Assistant(@JsonProperty("name")String name, @JsonProperty("surname")String surname, @JsonProperty("contract")String contract, @JsonProperty("work")double work, @JsonProperty("overtime")boolean overtime, @JsonProperty("worksDay")boolean worksDay, @JsonProperty("worksNight")boolean worksNight, @JsonProperty("comments")String comments){
        setName(name);
        setSurname(surname);
        setContractType(contract);
        setWorkTime(work);
        setLikesOvertime(overtime);
        setWorksOnlyDay(worksDay);
        setWorksOnlyNight(worksNight);
        setComments(comments);

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ArrayList<Client> getClientList() {
        return clientList;
    }

    public void setClientList(ArrayList<Client> clientList) {
        this.clientList = clientList;
    }
}
