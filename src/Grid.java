import javax.swing.JOptionPane;
import java.awt.image.RenderedImage;
import java.io.File;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import javax.swing.JComponent;


public class Grid extends JComponent implements KeyListener, MouseListener
{
    private Cell[][] cells;
    private JFrame frame;
    private int lastKeyPressed;
    private Location lastLocationClicked;
    private Color lineColor;
    
    public Grid(final int numRows, final int numCols) {
        this.init(numRows, numCols);
    }
    
    public Grid(final String imageFileName) {
        final BufferedImage image = this.loadImage(imageFileName);
        this.init(image.getHeight(), image.getWidth());
        this.showImage(image);
        this.setTitle(imageFileName);
    }
    
    private BufferedImage loadImage(final String imageFileName) {
        final URL url = this.getClass().getResource(imageFileName);
        if (url == null) {
            throw new RuntimeException("cannot find file:  " + imageFileName);
        }
        try {
            return ImageIO.read(url);
        }
        catch (IOException e) {
            throw new RuntimeException("unable to read from file:  " + imageFileName);
        }
    }
    
    public int getNumRows() {
        return this.cells.length;
    }
    
    public int getNumCols() {
        return this.cells[0].length;
    }
    
    private void init(final int numRows, final int numCols) {
        this.lastKeyPressed = -1;
        this.lastLocationClicked = null;
        this.lineColor = null;
        this.cells = new Cell[numRows][numCols];
        for (int row = 0; row < numRows; ++row) {
            for (int col = 0; col < numCols; ++col) {
                this.cells[row][col] = new Cell();
            }
        }
        (this.frame = new JFrame("Grid")).setDefaultCloseOperation(3);
        this.frame.addKeyListener(this);
        final int cellSize = Math.max(Math.min(500 / this.getNumRows(), 500 / this.getNumCols()), 1);
        this.setPreferredSize(new Dimension(cellSize * numCols, cellSize * numRows));
        this.addMouseListener(this);
        this.frame.getContentPane().add(this);
        this.frame.pack();
        this.frame.setVisible(true);
    }
    
    private void showImage(final BufferedImage image) {
        for (int row = 0; row < this.getNumRows(); ++row) {
            for (int col = 0; col < this.getNumCols(); ++col) {
                final int x = col * image.getWidth() / this.getNumCols();
                final int y = row * image.getHeight() / this.getNumRows();
                final int c = image.getRGB(x, y);
                final int red = (c & 0xFF0000) >> 16;
                final int green = (c & 0xFF00) >> 8;
                final int blue = c & 0xFF;
                this.cells[row][col].setColor(new Color(red, green, blue));
            }
        }
        this.repaint();
    }
    
    private int getCellSize() {
        final int cellWidth = this.getWidth() / this.getNumCols();
        final int cellHeight = this.getHeight() / this.getNumRows();
        return Math.min(cellWidth, cellHeight);
    }
    
    @Override
    public void keyPressed(final KeyEvent e) {
        this.lastKeyPressed = e.getKeyCode();
    }
    
    @Override
    public void keyReleased(final KeyEvent e) {
    }
    
    @Override
    public void keyTyped(final KeyEvent e) {
    }
    
    @Override
    public void mousePressed(final MouseEvent e) {
        final int cellSize = this.getCellSize();
        final int row = e.getY() / cellSize;
        if (row < 0 || row >= this.getNumRows()) {
            return;
        }
        final int col = e.getX() / cellSize;
        if (col < 0 || col >= this.getNumCols()) {
            return;
        }
        this.lastLocationClicked = new Location(row, col);
    }
    
    @Override
    public void mouseReleased(final MouseEvent e) {
    }
    
    @Override
    public void mouseClicked(final MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent e) {
    }
    
    @Override
    public void mouseExited(final MouseEvent e) {
    }
    
    private static java.awt.Color toJavaColor(final Color color) {
        return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
    }
    
    public void paintComponent(final Graphics g) {
        for (int row = 0; row < this.getNumRows(); ++row) {
            for (int col = 0; col < this.getNumCols(); ++col) {
                final Location loc = new Location(row, col);
                final Cell cell = this.cells[loc.getRow()][loc.getCol()];
                final Color color = cell.getColor();
                g.setColor(toJavaColor(color));
                final int cellSize = this.getCellSize();
                final int x = col * cellSize;
                final int y = row * cellSize;
                g.fillRect(x, y, cellSize, cellSize);
                final String imageFileName = cell.getImageFileName();
                if (imageFileName != null) {
                    final URL url = this.getClass().getResource(imageFileName);
                    if (url == null) {
                        System.out.println("File not found:  " + imageFileName);
                    }
                    else {
                        final Image image = new ImageIcon(url).getImage();
                        final int width = image.getWidth(null);
                        final int height = image.getHeight(null);
                        if (width > height) {
                            final int drawHeight = cellSize * height / width;
                            g.drawImage(image, x, y + (cellSize - drawHeight) / 2, cellSize, drawHeight, null);
                        }
                        else {
                            final int drawWidth = cellSize * width / height;
                            g.drawImage(image, x + (cellSize - drawWidth) / 2, y, drawWidth, cellSize, null);
                        }
                    }
                }
                if (this.lineColor != null) {
                    g.setColor(toJavaColor(this.lineColor));
                    g.drawRect(x, y, cellSize, cellSize);
                }
            }
        }
    }
    
    public void setTitle(final String title) {
        this.frame.setTitle(title);
    }
    
    public boolean isValid(final Location loc) {
        final int row = loc.getRow();
        final int col = loc.getCol();
        return row >= 0 && row < this.getNumRows() && col >= 0 && col < this.getNumCols();
    }
    
    public void setColor(final Location loc, final Color color) {
        if (!this.isValid(loc)) {
            throw new RuntimeException("cannot set color of invalid location " + loc + " to color " + color);
        }
        this.cells[loc.getRow()][loc.getCol()].setColor(color);
        this.repaint();
    }
    
    public Color getColor(final Location loc) {
        if (!this.isValid(loc)) {
            throw new RuntimeException("cannot get color from invalid location " + loc);
        }
        return this.cells[loc.getRow()][loc.getCol()].getColor();
    }
    
    public void setImage(final Location loc, final String imageFileName) {
        if (!this.isValid(loc)) {
            throw new RuntimeException("cannot set image for invalid location " + loc + " to \"" + imageFileName + "\"");
        }
        this.cells[loc.getRow()][loc.getCol()].setImageFileName(imageFileName);
        this.repaint();
    }
    
    public String getImage(final Location loc) {
        if (!this.isValid(loc)) {
            throw new RuntimeException("cannot get image for invalid location " + loc);
        }
        return this.cells[loc.getRow()][loc.getCol()].getImageFileName();
    }
    
    public static void pause(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (Exception ex) {}
    }
    
    public int checkLastKeyPressed() {
        final int key = this.lastKeyPressed;
        this.lastKeyPressed = -1;
        return key;
    }
    
    public Location checkLastLocationClicked() {
        final Location loc = this.lastLocationClicked;
        this.lastLocationClicked = null;
        return loc;
    }
    
    public void load(final String imageFileName) {
        this.showImage(this.loadImage(imageFileName));
        this.setTitle(imageFileName);
    }
    
    public void save(final String imageFileName) {
        try {
            final BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), 1);
            this.paintComponent(bi.getGraphics());
            final int index = imageFileName.lastIndexOf(46);
            if (index == -1) {
                throw new RuntimeException("invalid image file name:  " + imageFileName);
            }
            ImageIO.write(bi, imageFileName.substring(index + 1), new File(imageFileName));
        }
        catch (IOException e) {
            throw new RuntimeException("unable to save image to file:  " + imageFileName);
        }
    }
    
    public void setLineColor(final Color color) {
        this.lineColor = color;
        this.repaint();
    }
    
    public void showMessageDialog(final String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    public String showInputDialog(final String message) {
        return JOptionPane.showInputDialog(this, message);
    }
}
