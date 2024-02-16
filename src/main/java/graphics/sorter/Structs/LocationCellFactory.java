package graphics.sorter.Structs;

import graphics.sorter.Human;
import graphics.sorter.Location;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class LocationCellFactory implements Callback<ListView<Location>, ListCell<Location>> {
    @Override
    public ListCell<Location> call(ListView<Location> param) {
        return new ListCell<>(){
            @Override
            public void updateItem(Location loc, boolean empty) {
                super.updateItem(loc, empty);
                if (empty || loc == null) {
                    setText(null);
                } else {
                    setText(loc.getCasualName());
                }
            }
        };
    }
}

