package Orakas.controllers;
/*
Interface for all JavaFX controllers
 */
public interface ControllerInterface {
/*
Update screen to reflect changes done in other pages
 */
    void updateScreen();
    /*
Load data from database to reflect changes done in other pages
 */
    void loadAndUpdateScreen();

}
