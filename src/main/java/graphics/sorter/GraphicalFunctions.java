package graphics.sorter;public class GraphicalFunctions {
    public static String toHexWithAlphaString(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255)); // Include alpha
    }
    public static String toHexFromRGBO(javafx.scene.paint.Color color, double o) {
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (o * 255)); // Include alpha
    }
    public static String toHexFromRGBO(double red, double green, double blue, double o) {
        return String.format("#%02X%02X%02X%02X",
                (int) (red * 255),
                (int) (green * 255),
                (int) (blue * 255),
                (int) (o * 255)); // Include alpha
    }
}
