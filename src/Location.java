
public class Location
{
    private int row;
    private int col;
    
    public Location(final int r, final int c) {
        this.row = r;
        this.col = c;
    }
    
    public int getRow() {
        return this.row;
    }
    
    public int getCol() {
        return this.col;
    }
    
    public boolean equals(final Location otherLoc) {
        return this.row == otherLoc.getRow() && this.col == otherLoc.getCol();
    }
    
    @Override
    public String toString() {
        return "(" + this.row + ", " + this.col + ")";
    }
}
