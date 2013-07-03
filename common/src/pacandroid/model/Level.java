package pacandroid.model;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pacandroid.stats.HeatMap;

public class Level {

    public static final int GRID_UNIT_SIZE = 2;
    public static final int GRID_WIDTH = 24;
    public static final int GRID_HEIGHT = 15;
    public static int MAX_ENEMIES = 1;
    private final String name;
    private final List<Entity> entities;
    private final Grid wallGrid;
    private final HeatMap heatMap;
    private final HeatMap deathMap;
    private AndyAndroid andyAndroid;
    private int maxEnemies;
    private int enemyAI;
    private int enemySpeed;
    private Powerup currentPowerup;
    private int powerupTimeLeft;
    private Score score;
    private int lives;
    private float timeOnLevel;
    private boolean gameOver;
    private boolean playerWon;

    public Level() {
        this("test-level");
    }

    public Level(String name) {
        this.name = name;
        entities = new ArrayList<Entity>();
        wallGrid = new Grid(GRID_WIDTH, GRID_HEIGHT, GRID_UNIT_SIZE);

        heatMap = new HeatMap(GRID_WIDTH * GRID_UNIT_SIZE, GRID_HEIGHT * GRID_UNIT_SIZE, 4);
        deathMap = new HeatMap(GRID_WIDTH * GRID_UNIT_SIZE, GRID_HEIGHT * GRID_UNIT_SIZE, 4);

        maxEnemies = MAX_ENEMIES;
        setDefaults();
    }

    public void setDefaults() {
        setCurrentPowerup(Powerup.LevelStartFreeze);

        score = new Score();
        lives = 3;
    }

    public void spawnEntity(Entity entity) {
        entities.add(entity);

        if (entity instanceof AndyAndroid)
            andyAndroid = (AndyAndroid) entity;
        if (entity instanceof Apple)
            ((Apple) entity).setLevel(this);
    }

    public String getName() {
        return name;
    }

    public int getGridWidth() {
        return wallGrid.getWidth();
    }

    public int getGridHeight() {
        return wallGrid.getHeight();
    }

    public int getGridUnitSize() {
        return GRID_UNIT_SIZE;
    }

    public int getWorldWidth() {
        return wallGrid.getWidth() * GRID_UNIT_SIZE;
    }

    public int getWorldHeight() {
        return wallGrid.getHeight() * GRID_UNIT_SIZE;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public <T extends Entity> List<T> getEntitiesByType(Class<T> entityType) {
        List<T> list = new ArrayList<T>();

        for (Entity e : entities) {
            if (entityType.isInstance(e))
                list.add(entityType.cast(e));
        }

        return list;
    }

    public AndyAndroid getAndyAndroid() {
        return andyAndroid;
    }

    public int getMaxEnemies() {
        return MAX_ENEMIES;
    }

    public void setMaxEnemies(int maxEnemies) {
        this.maxEnemies = maxEnemies;
    }

    public Grid getGrid() {
        return wallGrid;
    }

    public HeatMap getHeatMap() {
        return heatMap;
    }

    public HeatMap getDeathMap() {
        return deathMap;
    }

    public void update(float timestep) {
        if (!getCurrentPowerup().freeze && !isGameOver()) {
            Iterator<Entity> itr = entities.iterator();
            while (itr.hasNext()) {
                Entity ent = itr.next();
                ent.update(timestep);
            }

            if (andyAndroid != null && !andyAndroid.isMarkedForKill()) {
                Vector2 gridPoint = andyAndroid.getPosition().cpy();
                gridPoint.y = (GRID_HEIGHT * GRID_UNIT_SIZE) - gridPoint.y;
                heatMap.addPoint(gridPoint);
            }
        }


        timeOnLevel += timestep;

        if (powerupTimeLeft != Integer.MIN_VALUE) {
            if (powerupTimeLeft == 0) {
                powerupTimeLeft = -1;
            } else {
                powerupTimeLeft -= (int) (timestep * 1000);
                if (powerupTimeLeft < 0)
                    setCurrentPowerup(Powerup.Null);
            }
        }
    }

    public void removeDead() {
        Iterator<Entity> itr = entities.iterator();
        while (itr.hasNext()) {
            Entity e = itr.next();
            if (e.isMarkedForKill()) {
                if (e instanceof AndyAndroid) {
                    Vector2 gridPoint = andyAndroid.getPosition().cpy();
                    gridPoint.y = (GRID_HEIGHT * GRID_UNIT_SIZE) - gridPoint.y;
                    deathMap.addPoint(gridPoint);
                }

                itr.remove();
            }
        }
    }

    public Score getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public float getTimeOnLevel() {
        return timeOnLevel;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean didPlayerWin() {
        return playerWon;
    }

    public void takeLife() {
        lives--;

        if (lives <= 0)
            setGameOver(false);
    }

    public void addLife() {
        lives++;
    }

    public void setGameOver(boolean playerWon) {
        this.gameOver = true;
        this.playerWon = playerWon;
    }

    public Powerup getCurrentPowerup() {
        return currentPowerup;
    }

    public void setCurrentPowerup(Powerup currentPowerup) {
        this.currentPowerup = currentPowerup;

        powerupTimeLeft = currentPowerup.buffMillis;
        if (powerupTimeLeft < 0) {
            powerupTimeLeft = Integer.MIN_VALUE;
        }
    }

    public void choosePowerup(int x, int y) {
        Powerup[] valid = new Powerup[]{
            Powerup.KillAll,
            Powerup.Edible,
            Powerup.DoubleScore,
            Powerup.NewLife};

        int hash = 23;
        hash = hash * 53 + x;
        hash = hash * 53 + y;
        hash %= valid.length;

        setCurrentPowerup(valid[hash]);
    }
}
