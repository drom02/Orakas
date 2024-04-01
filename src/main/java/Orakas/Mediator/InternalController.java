package Orakas.Mediator;

import Orakas.controllers.ControllerInterface;

import java.util.concurrent.CompletableFuture;

public class InternalController extends Controller {
    ControllerInterface controllerInterface;
    public InternalController(ControllerInterface controllerInterfaceI) {
        super(ControllerMediator.getControllerMediator());
        ControllerMediator.getControllerMediator().addController(this);
        controllerInterface = controllerInterfaceI;
    }
    @Override
    public void send(String message) {
        mediator.send(message, this);
    }

    @Override
    public void receive(String message) {
        switch (message){
            case "Assistant":
                 CompletableFuture.runAsync(()-> { controllerInterface.loadAndUpdateScreen();
                });
                break;
            case "Client":
                break;
            case "Date":
                break;
            default:
                System.out.println(message);
        }
    }
}
