package graphics.sorter.Structs;

import graphics.sorter.JsonManip;

import java.io.IOException;

public interface Saveable {
     void createNew(JsonManip map) throws IOException;

}
