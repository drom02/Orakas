package Orakas.Structs;

import Orakas.JsonManip;

import java.io.IOException;

public interface Saveable {
     void createNew(JsonManip map) throws IOException;

}
