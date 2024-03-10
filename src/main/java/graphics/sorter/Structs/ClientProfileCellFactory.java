package graphics.sorter.Structs;

import graphics.sorter.ClientProfile;
import graphics.sorter.Human;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

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

