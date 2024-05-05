package Orakas.Structs;

import Orakas.Humans.ClientProfile;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
/*
Custom cell factory for display of text in JavaFX list of client.
 */
public class ClientProfileCellFactory implements Callback<ListView<ClientProfile>, ListCell<ClientProfile>> {

        @Override
        public ListCell<ClientProfile> call(ListView<ClientProfile> param) {
            return new ListCell<>(){
                @Override
                public void updateItem(ClientProfile person, boolean empty) {
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

