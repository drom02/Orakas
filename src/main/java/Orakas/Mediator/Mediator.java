package Orakas.Mediator;
/*
Interface used for communication between view screen controllers
 */
public interface Mediator {
    void send(String message, Controller controller);
}
