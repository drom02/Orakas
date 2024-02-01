package graphics.sorter;

import java.util.UUID;

public class Human {

    private UUID ID;
    private String name;
    private  String surname;
    private String middlename;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public UUID getID() {
        return ID;
    }

    public void setID(UUID ID) {
        this.ID = ID;
    }
}
