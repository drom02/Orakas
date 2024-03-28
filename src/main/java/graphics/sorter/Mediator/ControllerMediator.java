package graphics.sorter.Mediator;

import graphics.sorter.Database;
import graphics.sorter.Settings;

import java.util.ArrayList;
import java.util.List;

public class ControllerMediator implements Mediator {
    private static ControllerMediator ControllerMediator;
    private List<Controller> controllers;
    public static ControllerMediator getControllerMediator(){
        if (ControllerMediator == null) {
            // if instance is null, initialize
            ControllerMediator = new ControllerMediator();
        }
        return ControllerMediator;
    }
    public ControllerMediator() {
        this.controllers = new ArrayList<>();
    }

    public void addController(Controller controller) {
        controllers.add(controller);
    }
    @Override
    public void send(String message, Controller originator) {
        for (Controller colleague : controllers) {
            // Don't tell the originator
            if (colleague != originator) {
                colleague.receive(message);
            }
        }
    }
}
