package Orakas.Structs;

import Orakas.JsonManip;

import java.io.IOException;
/*
Interface for all classes that can be saved into the database
 */
public interface Saveable {
     void createNew(JsonManip map) throws IOException;

}
