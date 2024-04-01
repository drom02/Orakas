package Orakas.Structs;

import Orakas.Humans.Human;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;


import javafx.util.Callback;

public class HumanCellFactory implements Callback<ListView<Human>, ListCell<Human>> {
    @Override
    public ListCell<Human> call(ListView<Human> param) {
        return new ListCell<>(){
            @Override
            public void updateItem(Human person, boolean empty) {
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

