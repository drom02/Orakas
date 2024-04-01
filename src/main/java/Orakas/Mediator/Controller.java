package Orakas.Mediator;

public abstract class Controller {
    protected Mediator mediator;
    public Controller(Mediator mediator) {
        this.mediator = mediator;
    }
    public abstract void send(String message);
    public abstract void receive(String message);
}
