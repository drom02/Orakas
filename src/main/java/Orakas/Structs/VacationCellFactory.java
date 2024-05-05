package Orakas.Structs;

import Orakas.Vacations.Vacation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
/*
Cell factory that allows for custom display in JavaFX list.
 */
public class VacationCellFactory implements Callback<ListView<Vacation>, ListCell<Vacation>> {
    @Override
    public ListCell<Vacation> call(ListView<Vacation> param) {
        return new ListCell<>(){
            @Override
            public void updateItem(Vacation vac, boolean empty) {
                super.updateItem(vac, empty);
                if (empty || vac == null) {
                    setText(null);
                } else {
                    setText("Od " + vac.getStart().toString() + " do " + vac.getEnd().toString());
                }
            }
        };
    }
}
