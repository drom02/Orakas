package graphics.sorter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class GraphicalSettings {
    public HashMap<String, String> getColors() {
        return colors;
    }

    public void setColors(HashMap<String, String> colors) {
        this.colors = colors;
    }

    public HashMap<String, String> getFonts() {
        return fonts;
    }

    public void setFonts(HashMap<String, String> fonts) {
        this.fonts = fonts;
    }

    private HashMap<String,String> colors;
    private HashMap<String,String> fonts;
    @JsonCreator
    public GraphicalSettings(@JsonProperty("colors") HashMap colors,@JsonProperty("fonts") HashMap fonts){
        if(colors==null){
            setColors(new HashMap());
            populate();
        }else{
        setColors(colors);
        }
        if(fonts==null){
            setFonts(new HashMap<>());
            populate();
        }else{
            setFonts(fonts);
        }
    }
    private void populate(){
        //region colors
        getColors().put("Day", GraphicalFunctions.toHexFromRGBO(Color.GRAY,0.1));
        getColors().put("Night", GraphicalFunctions.toHexFromRGBO(Color.BLACK,0.75));
        getColors().put("Error",GraphicalFunctions.toHexFromRGBO(Color.RED,1.00));
        getColors().put("PrimaryColor", GraphicalFunctions.toHexFromRGBO(Color.BLUE,1.00));
        getColors().put("SecondaryColor", GraphicalFunctions.toHexFromRGBO(Color.GREEN,1.00));
        getColors().put("TertiaryColor", GraphicalFunctions.toHexFromRGBO(Color.ORANGE,1.00));
        getColors().put("","");

        //regionend
    }

}
