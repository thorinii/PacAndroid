package pacandroid.controller;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import pacandroid.exceptions.GameException;
import pacandroid.model.AndyAndroid;
import pacandroid.model.Apple;
import pacandroid.model.Entity;
import pacandroid.model.Grid;
import pacandroid.model.Level;
import pacandroid.model.Powerup;
import pacandroid.util.Timers;

public class LevelController {

    private final Level level;
    private boolean left, right, up, down;
    private Vector2 touchControl;
    private int ticks;

    public LevelController(Level level) {
        this.level = level;

        this.ticks = 0;
    }

    public void leftPressed() {
        left = true;
    }

    public void rightPressed() {
        right = true;
    }

    public void upPressed() {
        up = true;
    }

    public void downPressed() {
        down = true;
    }

    public void leftReleased() {
        left = false;
    }

    public void rightReleased() {
        right = false;
    }

    public void upReleased() {
        up = false;
    }

    public void downReleased() {
        down = false;
    }

    public void setTouchControl(Vector2 touchControl) {
        this.touchControl = touchControl;
    }

    public void update(float delta) throws GameException {
        ticks++;

        processInput();

        spawnAndroid(level);
        spawnApple(level);

        detectCollisions();

        processPowerups();

        Timers.update(delta);
    }

    private void spawnAndroid(Level l) {
        Grid g = l.getGrid();

        if (l.getAndyAndroid().isMarkedForKill()) {
            for (int i = 0; i < g.getWidth(); i++) {
                for (int j = 0; j < g.getHeight(); j++) {
                    if (g.get(i, j) == Grid.GRID_ANDROID_SPAWN) {
                        AndyAndroid entity = new AndyAndroid(g, level);
                        entity.setPosition(new Vector2(
                                i * Level.GRID_UNIT_SIZE, j
                                * Level.GRID_UNIT_SIZE));

                        l.spawnEntity(entity);

                        killNearbyApples(i * Level.GRID_UNIT_SIZE, j
                                * Level.GRID_UNIT_SIZE, 3);

                        return;
                    }
                }
            }
        }
    }

    private void killNearbyApples(int x, int y, int dist) {
        for (Apple a : level.getEntitiesByType(Apple.class)) {
            System.out.println(a.getPosition().dst(x, y));

            if (a.getPosition().dst(x, y) < dist) {
                a.markForKill();
            }
        }
    }

    private void spawnApple(Level l) {
        Grid g = l.getGrid();

        if (level.getCurrentPowerup() == Powerup.KillAll)
            return;

        if (l.getEntitiesByType(Apple.class).size() < l.getMaxEnemies()) {
            for (int i = 0; i < g.getWidth(); i++) {
                for (int j = 0; j < g.getHeight(); j++) {
                    if (g.get(i, j) == Grid.GRID_ENEMY_SPAWN && randomTime(50)) {
                        int[] xy = findNearestEmpty(l, i, j);

                        Apple entity = new Apple(g);
                        entity.setLevel(level);
                        entity.setPosition(new Vector2(
                                xy[0] * Level.GRID_UNIT_SIZE,
                                xy[1] * Level.GRID_UNIT_SIZE));

                        l.spawnEntity(entity);

                        return;
                    }
                }
            }
        }
    }

    private boolean randomTime(int averageTicks) {
        return ticks % (int) (Math.random() * averageTicks + averageTicks / 2) == 0;
    }

    private int[] findNearestEmpty(Level l, int x, int y) {
        Grid g = l.getGrid();
        int[] xy = new int[2];
        xy[0] = xy[1] = -1;

        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                int space;

                space = g.get(x + i, y + j, Grid.GRID_WALL);
                if (space == Grid.GRID_EMPTY || space == Grid.GRID_JELLYBEAN) {
                    xy[0] = x + i;
                    xy[1] = y + j;
                    return xy;
                }


                space = g.get(x - i, y + j, Grid.GRID_WALL);
                if (space == Grid.GRID_EMPTY || space == Grid.GRID_JELLYBEAN) {
                    xy[0] = x - i;
                    xy[1] = y + j;
                    return xy;
                }


                space = g.get(x + i, y - j, Grid.GRID_WALL);
                if (space == Grid.GRID_EMPTY || space == Grid.GRID_JELLYBEAN) {
                    xy[0] = x + i;
                    xy[1] = y - j;
                    return xy;
                }


                space = g.get(x - i, y - j, Grid.GRID_WALL);
                if (space == Grid.GRID_EMPTY || space == Grid.GRID_JELLYBEAN) {
                    xy[0] = x - i;
                    xy[1] = y - j;
                    return xy;
                }
            }
        }

        return xy;
    }

    private void processInput() {
        if (left || right || up || down) {
            if (left) {
                level.getAndyAndroid()
                        .setVelocity(new Vector2(-AndyAndroid.REGULAR_SPEED, 0));
            } else if (right) {
                level.getAndyAndroid()
                        .setVelocity(new Vector2(AndyAndroid.REGULAR_SPEED, 0));
            }

            if (up) {
                level.getAndyAndroid()
                        .setVelocity(new Vector2(0, AndyAndroid.REGULAR_SPEED));
            } else if (down) {
                level.getAndyAndroid()
                        .setVelocity(new Vector2(0, -AndyAndroid.REGULAR_SPEED));
            }
        } else if (touchControl != null && touchControl.len() > 0) {
            level.getAndyAndroid()
                    .setVelocity(
                    touchControl.cpy()
                    .mul(AndyAndroid.REGULAR_SPEED));
        }
    }

    private void detectCollisions() throws GameException {
        List<Entity> entities = new ArrayList<Entity>(level.getEntities());

        for (Entity e : entities) {
            Vector2 epos = e.getPosition();
            Vector2 ebounds = e.getBounds();

            for (Entity o : entities) {
                if (o == e)
                    continue;

                Vector2 opos = o.getPosition();
                Vector2 obounds = o.getBounds();

                // If they intersect
                if (epos.x < opos.x + obounds.x
                        && epos.x + ebounds.x > opos.x
                        && epos.y < opos.y + obounds.y
                        && epos.y + ebounds.y > opos.y)
                    e.collideWith(o);
            }
        }
    }

    /*
     * Processes some transient powerups
     */
    private void processPowerups() {
        switch (level.getCurrentPowerup()) {
            case DoubleScore:
                level.getScore().doubleScore();
                break;
            case NewLife:
                level.addLife();
                break;
        }
    }
}
