package pacandroid.model;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.lachlanap.lct.Constant;
import pacandroid.stats.HeatMap;

public class Level {

    public static final int GRID_UNIT_SIZE = 2;
    public static final int GRID_WIDTH = 24;
    public static final int GRID_HEIGHT = 15;
    @Constant(name = "Max Enemies", constraints = "0,")
    public static int MAX_ENEMIES = 1;
    private final List<Entity> entities;
    private final Grid wallGrid;
    private final HeatMap heatMap;
    private final HeatMap deathMap;
    private AndyAndroid andyAndroid;
    private int maxEnemies;
    private int enemyAI;
    private int enemySpeed;

    public Level() {
        entities = new ArrayList<Entity>();
        wallGrid = new Grid(GRID_WIDTH, GRID_HEIGHT, GRID_UNIT_SIZE);

        heatMap = new HeatMap(GRID_WIDTH * GRID_UNIT_SIZE, GRID_HEIGHT * GRID_UNIT_SIZE, 4);
        deathMap = new HeatMap(GRID_WIDTH * GRID_UNIT_SIZE, GRID_HEIGHT * GRID_UNIT_SIZE, 4);

        maxEnemies = MAX_ENEMIES;
    }

    public void spawnEntity(Entity entity) {
        entities.add(entity);

        if (entity instanceof AndyAndroid)
            andyAndroid = (AndyAndroid) entity;
        if (entity instanceof Apple)
            ((Apple) entity).setLevel(this);
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
}
