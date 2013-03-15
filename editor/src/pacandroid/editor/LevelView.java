package pacandroid.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import pacandroid.model.Grid;
import pacandroid.model.Level;

public class LevelView extends JPanel {

    private static final Color GRID_COLOUR = new Color(200, 200, 200, 150);
    private Level level;
    private final Point workingSpace;
    private final Point workingOffset;
    private double gridSize;
    private Image[] cellImages;

    public LevelView() {
        level = new Level();
        workingSpace = new Point(0, 0);
        workingOffset = new Point(0, 0);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        loadImages();
    }

    private void loadImages() {
        cellImages = new Image[6];
        cellImages[0] = null;

        try {
            cellImages[Grid.GRID_WALL] = ImageIO.read(getClass()
                    .getResource("/640x/wall-32x32.png"));
            cellImages[Grid.GRID_JELLYBEAN] = ImageIO.read(getClass()
                    .getResource("/640x/jellybean-32x32-1.png"));

            cellImages[Grid.GRID_ANDROID_SPAWN] = ImageIO.read(getClass()
                    .getResource("/640x/andyandroid-30x30.png"));
            cellImages[Grid.GRID_ENEMY_SPAWN] = null;
            cellImages[Grid.GRID_POWERUP] = ImageIO.read(getClass()
                    .getResource("/640x/icecream-32x32-2.png"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    private void recalculateWorkingSpace() {
        Dimension size = getSize();
        double x = size.width;
        double y = size.height;

        x /= Level.GRID_WIDTH;
        y /= Level.GRID_HEIGHT;

        if (x < y) {
            // We have less width than height (for the standard aspect ratio)

            workingSpace.x = (int) (x * Level.GRID_WIDTH);
            workingSpace.y = (int) (x * Level.GRID_HEIGHT);
        } else {
            workingSpace.x = (int) (y * Level.GRID_WIDTH);
            workingSpace.y = (int) (y * Level.GRID_HEIGHT);
        }

        workingOffset.x = size.width / 2 - workingSpace.x / 2;
        workingOffset.y = size.height / 2 - workingSpace.y / 2;

        gridSize = ((double) workingSpace.x) / Level.GRID_WIDTH;
        ;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        recalculateWorkingSpace();

        g.setColor(Color.WHITE);
        g.fillRect(workingOffset.x, workingOffset.y, workingSpace.x,
                   workingSpace.y);

        drawLevel(g);
        drawGrid(g);
    }

    public Point mouseToGridCoords(Point mouse) {
        Point grid = new Point();

        grid.x = (int) ((mouse.x - workingOffset.x) / gridSize);
        grid.y = (int) ((mouse.y - workingOffset.y) / gridSize);

        return grid;
    }

    private void drawLevel(Graphics g) {
        Grid grid = level.getGrid();
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                int x = (int) (workingOffset.x + gridSize * i);
                int y = (int) (workingOffset.y + gridSize * j);
                Color colour;

                switch (grid.get(i, j)) {
                    case Grid.GRID_WALL:
                        colour = Color.BLACK;
                        break;
                    case Grid.GRID_ENEMY_SPAWN:
                        colour = Color.RED;
                        break;
                    default:
                        colour = Color.WHITE;
                }


                g.setColor(colour);
                g.fillRect(x, y, (int) Math.ceil(gridSize), (int) Math
                        .ceil(gridSize));

                if (cellImages[grid.get(i, j)] != null) {
                    Image img = cellImages[grid.get(i, j)];
                    g.drawImage(img, x, y, (int) gridSize, (int) gridSize, null);
                }
            }
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(GRID_COLOUR);

        int lineX;
        int lineY;

        lineY = workingSpace.y;
        for (int i = 1; i < Level.GRID_WIDTH; i++) {
            lineX = (int) (i * gridSize);

            g.drawLine(workingOffset.x + lineX, workingOffset.y,
                       workingOffset.x + lineX, workingOffset.y + lineY);
        }

        lineX = workingSpace.x;
        for (int i = 1; i < Level.GRID_HEIGHT; i++) {
            lineY = (int) (i * gridSize);

            g.drawLine(workingOffset.x, workingOffset.y + lineY,
                       workingOffset.x + lineX, workingOffset.y + lineY);
        }
    }
}
