package pacandroid.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * An entity locked to along grid. Any velocity will be confined to one
 * direction at a time.
 * <p/>
 * If the entity enters filled grid space by a preset amount, its velocity will
 * be reversed.
 * <p/>
 * @author lachlan
 * <p/>
 */
public abstract class GridLockedDynamicEntity extends DynamicEntity {

    public static final float GRID_SNAP_DISTANCE = 0.1f;
    private final Grid grid;
    private final Vector2 gridPosition;
    private final float gridSnapDistance;

    public GridLockedDynamicEntity(Vector2 bounds, Grid grid) {
        super(bounds);
        this.grid = grid;
        this.gridPosition = new Vector2();
        this.gridSnapDistance = GRID_SNAP_DISTANCE * grid.getUnitSize();
    }

    public Grid getGrid() {
        return grid;
    }

    @Override
    public void setPosition(Vector2 position) {
        super.setPosition(position);

        gridPosition.x = getPosition().x / grid.getUnitSize();
        gridPosition.y = getPosition().y / grid.getUnitSize();
    }

    /**
     * Filters out any changes in velocity, such that the velocity is confined
     * to one direction. Note it will modify the passed Vector2 if necessary.
     */
    @Override
    public void setVelocity(Vector2 velocity) {
        float xAbs = Math.abs(velocity.x);
        float yAbs = Math.abs(velocity.y);
        Vector2 pos = getPosition();

        if (xAbs > yAbs) {
            yAbs = 0;
            velocity.y = 0;
        } else {
            xAbs = 0;
            velocity.x = 0;
        }

        if (xAbs > 0) {
            if (Math.abs(gridPosition.y % 1) > gridSnapDistance)
                return;

            if (velocity.x > 0) {
                if (gridPosition.x >= (grid.getWidth() - 1)
                        || Grid.isWall(grid.get((int) gridPosition.x + 1,
                                                (int) gridPosition.y))) {
                    return;
                }
            } else if (gridPosition.x <= 1
                    || Grid.isWall(grid.get((int) gridPosition.x - 1,
                                            (int) gridPosition.y))) {
                return;
            }

            pos.y = Math.round(gridPosition.y)
                    * grid.getUnitSize();
        } else if (yAbs > 0) {
            if (Math.abs(gridPosition.x % 1) > gridSnapDistance)
                return;

            if (velocity.y > 0) {
                if (gridPosition.y >= (grid.getHeight() - 1)
                        || Grid.isWall(grid.get((int) gridPosition.x,
                                                (int) gridPosition.y + 1))) {
                    return;
                }
            } else if (gridPosition.y <= 1
                    || Grid.isWall(grid.get((int) gridPosition.x,
                                            (int) gridPosition.y - 1))) {
                return;
            }

            pos.x = Math.round(gridPosition.x)
                    * grid.getUnitSize();
        }


        super.setVelocity(velocity);
    }

    /**
     * Updates the entity. If the entity is now in filled grid space more than
     * <code>error</code>, then it will reverse the velocity.
     */
    @Override
    public void update(float timestep) {
        Vector2 oldpos = getPosition().cpy();

        super.update(timestep);

        Vector2 pos = getPosition();
        Vector2 vel = getVelocity();

        boolean toggleVelocity = false;
        boolean stopVelocity = false;

        int gridIx = (int) (pos.x / grid.getUnitSize());
        int gridIy = (int) (pos.y / grid.getUnitSize());

        if (gridIx < 0 || gridIx >= (grid.getWidth() - 1)) {
            toggleVelocity = true;
        } else if (gridIy < 0 || gridIy >= (grid.getHeight() - 1)) {
            toggleVelocity = true;
        } else {
            if (vel.x > 0) {
                gridIx++;
            } else if (vel.x == 0) {
                if (vel.y > 0) {
                    gridIy++;
                }
            }

            if (Grid.isWall(grid.get(gridIx, gridIy))) {
                if (vel.x > 0) {
                    stopVelocity = true;
                    float gridLocation = (gridIx - 1) * grid.getUnitSize();

                    if (stopVelocity)
                        pos.x = gridLocation;
                } else if (vel.x < 0) {
                    stopVelocity = true;
                    float gridLocation = (gridIx + 1) * grid.getUnitSize();

                    if (stopVelocity)
                        pos.x = gridLocation;
                } else if (vel.y > 0) {
                    stopVelocity = true;
                    float gridLocation = (gridIy - 1) * grid.getUnitSize();

                    if (stopVelocity)
                        pos.y = gridLocation;
                } else if (vel.y < 0) {
                    stopVelocity = true;
                    float gridLocation = (gridIy + 1) * grid.getUnitSize();

                    if (stopVelocity)
                        pos.y = gridLocation;
                }
            } else if (grid.get(gridIx, gridIy) != Grid.GRID_EMPTY) {
                if (handleGridSpace(gridIx, gridIy, grid.get(gridIx, gridIy))) {
                    grid.set(gridIx, gridIy, Grid.GRID_EMPTY);
                }
            }
        }

        if (toggleVelocity)
            super.setVelocity(getVelocity().mul(-1));
        if (stopVelocity)
            super.setVelocity(getVelocity().mul(0));

        gridPosition.x = getPosition().x / grid.getUnitSize();
        gridPosition.y = getPosition().y / grid.getUnitSize();


        if (Grid.isWall(grid.getAt(pos.x, pos.y))) {
            StringBuilder sb = new StringBuilder("Mighty Big Problem: ");
            sb.append("entity in wall block ");
            sb.append(String.format("[%d,%d] -> [%d,%d] ",
                                    oldpos.x, oldpos.y,
                                    pos.x, pos.y));
            sb.append(String.format("([%d,%d])",
                                    vel.x, vel.y));
            sb.append(". dt: ").append(timestep);

            Gdx.app.error("physics", sb.toString());
        }
    }

    /**
     * Handler when the entity moves into a new grid space. Used for jellybeans
     * and such.
     * <p/>
     * @param x
     * the grid x
     * @param y
     * the grid y
     * @param gridSpace
     * the contents of the grid space
     * @return whether or not to 'consume' the grid space - set it to empty
     */
    protected boolean handleGridSpace(int x, int y, int gridSpace) {
        return false;
    }
}
