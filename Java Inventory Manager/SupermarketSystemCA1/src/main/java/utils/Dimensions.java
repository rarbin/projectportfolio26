package utils;

public class Dimensions implements java.io.Serializable {
    private final double length;
    private final double width;
    private final double posX;
    private final double posY;
    private final double orientation;

    public Dimensions(double length, double width, double posX, double posY, double orientation) {
        this.length = length;
        this.width = width;
        this.posX = posX;
        this.posY = posY;
        this.orientation = orientation;
    }

    // getters
    public double getLength() { return length; }
    public double getWidth() { return width; }
    public double getPosX() { return posX; }
    public double getPosY() { return posY; }
    public double getOrientation() { return orientation; }
    public double getArea() { return length * width; }

    @Override
    public String toString() {
        return length + "m * " + width + "m at (" + posX + "," + posY + ") " + orientation + " degrees.";
    }
}