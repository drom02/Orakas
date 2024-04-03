package Orakas;

import Orakas.Vacations.VacationTemp;
import Orakas.Vacations.Vacation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;

public class AssistantVacationManager {

    public void parseDate(DatePicker data){
       // String[] parts = data.getValue().toString().split("-");
        LocalDate t = data.getValue();
    }
    public void newVacation(LocalDate start, LocalDate end, boolean type){
        Vacation vac = new Vacation(start,end,type);
    }
    public static void onMouseClickedObs(LocalDate locd, TextFlow flow){
        flow.getChildren().clear();
        flow.getChildren().add(new Text(locd.toString()));
    }
    public static void loadVacationList(VacationTemp temp, ListView<Vacation> vacationList){
        ObservableList<Vacation> observVacationList = FXCollections.observableList(temp.getTempVacation());
        vacationList.setItems(observVacationList);
    }
}
