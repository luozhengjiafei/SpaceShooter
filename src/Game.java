import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;



public class Game
{
    private Grid grid;
    private int userRow;
    private int msElapsed;
    private int timesGet;
    private int timesAvoid;
    private int userColumn;
    
    public Game() {
        this.grid = new Grid(5, 10);
        this.userRow = 0;
        this.msElapsed = 0;
        this.timesGet = 0;
        this.timesAvoid = 3;
        this.userColumn = 0;
        this.updateTitle();
        this.grid.setImage(new Location(this.userRow, this.userColumn), "user.png");
    }
    
    public void play() {
        this.playSound("intro.wav");
        while (!this.isGameOver()) {
            Grid.pause(100);
            this.handleKeyPress();
            if (this.msElapsed % 300 == 0 && this.timesGet > 250) {
                this.scrollLeft();
                this.populateRightEdge();
            }
            this.updateTitle();
            this.msElapsed += 100;
            if (this.msElapsed % 300 == 0 && this.timesGet <= 250) {
                this.scrollLeft();
                this.populateRightEdge();
            }
            this.updateTitle();
            this.msElapsed += 100;
        }
        if (this.timesAvoid <= 0) {
            this.grid.showMessageDialog("Game over , your score is " + this.timesGet);
        }
        else {
            this.grid.showMessageDialog("You Won !");
        }
    }
    
    public void handleKeyPress() {
        final int key = this.grid.checkLastKeyPressed();
        if (key == 40 && this.userRow != 4) {
            this.grid.setImage(new Location(this.userRow, this.userColumn), (String)null);
            ++this.userRow;
            this.grid.setImage(new Location(this.userRow, this.userColumn), "user.png");
        }
        else if (key == 38 && this.userRow != 0) {
            this.grid.setImage(new Location(this.userRow, this.userColumn), (String)null);
            --this.userRow;
            this.grid.setImage(new Location(this.userRow, this.userColumn), "user.png");
        }
    }
    
    public void populateRightEdge() {
        final int row = this.grid.getNumRows();
        final int column = this.grid.getNumCols();
        final int random = (int)(Math.random() * 4.0);
        final int random_1 = (int)(Math.random() * row);
        if (random > 1) {
            if (random <= 2 && random > 1) {
                this.grid.setImage(new Location(random_1, column - 1), "get.png");
            }
            else if (random <= 3 && random > 2) {
                this.grid.setImage(new Location(random_1, column - 1), "avoid.png");
            }
        }
    }
    
    public void scrollLeft() {
        final String userimage = this.grid.getImage(new Location(this.userRow, 0));
        final int rows = this.grid.getNumRows();
        final int columns = this.grid.getNumCols();
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns - 1; ++column) {
                final String pic = this.grid.getImage(new Location(row, column + 1));
                if (column < 9 && this.grid.getImage(new Location(row, column + 1)) != null) {
                    this.grid.setImage(new Location(row, column), pic);
                    this.grid.setImage(new Location(row, column + 1), (String)null);
                }
                else {
                    this.handleCollision(new Location(this.userRow, 0));
                    if (column == 0 && this.grid.getImage(new Location(row, column)) != userimage) {
                        this.grid.setImage(new Location(row, column), (String)null);
                    }
                }
            }
        }
    }
    
    public void handleCollision(final Location loc) {
        if (this.grid.getImage(loc) != null) {
            this.updateTitle();
            if (this.grid.getImage(loc).equals("get.png")) {
                this.grid.setImage(loc, (String)null);
                this.grid.setImage(new Location(this.userRow, 0), "user.png");
                this.timesGet += 25;
                this.playSound("coin.wav");
                if (this.timesGet % 100 == 0) {
                    ++this.timesAvoid;
                    this.playSound("Power.wav");
                }
                this.updateTitle();
            }
            else if (this.grid.getImage(loc).equals("avoid.png")) {
                this.grid.setImage(loc, (String)null);
                this.grid.setImage(new Location(this.userRow, 0), "user.png");
                --this.timesAvoid;
                this.playSound("Explosion.wav");
                this.updateTitle();
            }
        }
    }
    
    public int getScore() {
        return this.timesGet;
    }
    
    public void playSound(final String soundFile) {
        final File f = new File(soundFile);
        try {
            final AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
            final Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getLife() {
        return this.timesAvoid;
    }
    
    public void updateTitle() {
        this.grid.setTitle("Points :  " + this.getScore() + " and Your still have " + this.getLife() + " life left.");
    }
    
    public boolean isGameOver() {
        if (this.timesAvoid <= 0) {
            this.playSound("Psycho.wav");
            return true;
        }
        if (this.timesGet == 500) {
            this.playSound("End.wav");
            return true;
        }
        return false;
    }
    
    public static void test() {
        final Game game = new Game();
        game.play();
    }
    
    public static void main(final String[] args) {
        test();
    }
}
