package Orakas.controllers;

import java.util.ArrayList;
import java.util.Objects;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
/*
Abstract class used for JavaFX view that can create and manipulate with unique objects.
Serves to enforce verification of mandatory fields when creating or changing unique object.
 */
public abstract class SaveableControllerInterface {
    public abstract void addToRequiredFields(Node item);
    public abstract Object getRequiredFields(int index);
    public abstract ArrayList<Node> getRequiredFields();
     abstract boolean verifyRequired();
    abstract void save();
    abstract void saveNew();
}
