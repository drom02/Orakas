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

    public int[] getDeftStart() {
        return deftStart;
    }

    public void setDeftStart(int[] deftStart) {
        this.deftStart = deftStart;
    }

    public int[] getDefEnd() {
        return defEnd;
    }

    public void setDefEnd(int[] defEnd) {
        this.defEnd = defEnd;
    }
    public int getMaxShiftLength() {
        return maxShiftLength;
    }

    public void setMaxShiftLength(int maxShiftLength) {
        this.maxShiftLength = maxShiftLength;
    }
    private int currentYear;
    private int currentMonth;

    private int[] deftStart;
    private int[] defEnd;
    private int maxShiftLength;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private String filePath;

    public void createNewSettingsFile() throws IOException {
        JsonManip jsom = new JsonManip();
        deftStart = new int[]{8,30};
        defEnd = new int[]{20,30};
        Settings defset = new Settings(12,2024, "E:\\JsonWriteTest\\",deftStart,defEnd,16);
        jsom.saveSettings(defset, defset.getFilePath());

    }
    @JsonCreator
    public Settings(@JsonProperty("currentMonth")int currentMonth,@JsonProperty("currentYear")int currentYear,@JsonProperty("filePath")String filePath,@JsonProperty("defStart")int[] defstart,@JsonProperty("defend")int[] defend,@JsonProperty("maxShiftLength")int maxShiftLength){
            setCurrentMonth(currentMonth);
            setCurrentYear(currentYear);
            setFilePath(filePath);
            setDeftStart(defstart);
            setDefEnd(defend);
            setMaxShiftLength(maxShiftLength);
    }
}
