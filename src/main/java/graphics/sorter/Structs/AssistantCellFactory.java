package graphics.sorter.Structs;

import graphics.sorter.Assistant;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;


import javafx.util.Callback;

public class AssistantCellFactory implements Callback<ListView<Assistant>, ListCell<Assistant>> {
    @Override
    public ListCell<Assistant> call(ListView<Assistant> param) {
        return new ListCell<>(){
            @Override
            public void updateItem(Assistant person, boolean empty) {
                super.updateItem(person, empty);
                if (empty || person == null) {
                    setText(null);
                } else {
                    setText(person.getName() + " " + person.getSurname());
                }
            }
        };
    }
}

