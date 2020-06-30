
public class Color
{
    private int red;
    private int green;
    private int blue;
    
    public Color(final int r, final int g, final int b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }
    
    public int getRed() {
        return this.red;
    }
    
    public int getGreen() {
        return this.green;
    }
    
    public int getBlue() {
        return this.blue;
    }
    
    public boolean equals(final Color otherColor) {
        return this.red == otherColor.getRed() && this.green == otherColor.getGreen() && this.blue == otherColor.getBlue();
    }
    
    @Override
    public String toString() {
        return "(" + this.red + ", " + this.green + ", " + this.blue + ")";
    }
}
