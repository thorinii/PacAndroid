package pacandroid.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.lachlanap.lct.Constant;

public class Level {

    public static final int GRID_UNIT_SIZE = 2;
    public static final int GRID_HEIGHT = 15;
    public static final int GRID_WIDTH = 24;
    @Constant(name = "Max Enemies", constraints = "0,")
    public static int MAX_ENEMIES = 1;
    private final List<Entity> entities;
    private final Grid wallGrid;
    private AndyAndroid andyAndroid;
    private int maxEnemies;
    private int enemyAI;
    private int enemySpeed;

    public Level() {
        entities = new ArrayList<Entity>();
        wallGrid = new Grid(Level.GRID_WIDTH, Level.GRID_HEIGHT, GRID_UNIT_SIZE);
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

    public void update(float timestep) {
        Iterator<Entity> itr = entities.iterator();
        while (itr.hasNext()) {
            Entity ent = itr.next();
            ent.update(timestep);
        }
    }

    public void removeDead() {
        Iterator<Entity> itr = entities.iterator();
        while (itr.hasNext()) {
            if (itr.next().isMarkedForKill())
                itr.remove();
        }
    }
}
