package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;

public class Settings {
    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    private int currentYear;
    private int currentMonth;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private String filePath;

    public void createNewSettingsFile() throws IOException {
        JsonManip jsom = new JsonManip();
        Settings defset = new Settings(12,2024, "E:\\JsonWriteTest\\");
        jsom.saveSettings(defset, defset.getFilePath());
    }
    @JsonCreator
    public Settings(@JsonProperty("currentMonth")int currentMonth,@JsonProperty("currentYear")int currentYear,@JsonProperty("filePath")String filePath ){
            setCurrentMonth(currentMonth);
            setCurrentYear(currentYear);
            setFilePath(filePath);
    }
}
