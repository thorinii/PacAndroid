package pacandroid.editor;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import pacandroid.model.Grid;

public class GridTool extends MouseAdapter {

    private LevelView levelView;
    private int desiredCell;
    private boolean mouseDown;

    public GridTool() {
        levelView = null;
        desiredCell = -1;
    }

    public void setLevelView(LevelView levelView) {
        if (this.levelView != null) {
            this.levelView.removeMouseListener(this);
            this.levelView.removeMouseMotionListener(this);
        }

        this.levelView = levelView;
        this.levelView.addMouseListener(this);
        this.levelView.addMouseMotionListener(this);
    }

    public LevelView getLevelView() {
        return levelView;
    }

    public void setDesiredCell(int desiredCell) {
        this.desiredCell = desiredCell;
    }

    public int getDesiredCell() {
        return desiredCell;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseDown)
            performTool(e.getPoint());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        performTool(e.getPoint());
        mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        performTool(e.getPoint());
        mouseDown = false;
    }

    private void performTool(Point mouse) {
        if (levelView != null && desiredCell != -1) {
            Point gridCoords = levelView.mouseToGridCoords(mouse);
            Grid grid = levelView.getLevel().getGrid();

            grid.set(gridCoords.x, gridCoords.y, desiredCell);

            levelView.repaint();
        }
    }
}
