
public class Cell
{
    private Color color;
    private String imageFileName;
    
    public Cell() {
        this.color = new Color(0, 0, 0);
        this.imageFileName = null;
    }
    
    public void setColor(final Color c) {
        this.color = c;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public String getImageFileName() {
        return this.imageFileName;
    }
    
    public void setImageFileName(final String fileName) {
        this.imageFileName = fileName;
    }
}
