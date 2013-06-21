package pacandroid.model;

import com.badlogic.gdx.math.Vector2;
import me.lachlanap.lct.Constant;
import pacandroid.model.LevelState.Powerup;
import pacandroid.model.PathFinder.Node;
import pacandroid.model.PathFinder.Path;
import pacandroid.util.MathUtil;

public class Apple extends DynamicEntity {

    public static final int AI_TRACK = 0;
    public static final int AI_PATHFIND = 1;
    @Constant(name = "Chase Speed", constraints = "0,")
    public static float CHASE_SPEED = 15f;
    @Constant(name = "Wander Speed", constraints = "0,")
    public static float WANDER_SPEED = 9f;
    @Constant(name = "Collision Response", constraints = "-100,100")
    public static float COLLISION_RESPONSE = -50f;
    @Constant(name = "Damping", constraints = "0,1")
    public static float DAMPING = .97f;
    private final Grid grid;
    private Level level;
    private int ticks;
    private final LevelState levelState;
    private Vector2 desired = new Vector2();
    private Vector2 steering = new Vector2();
    private PathFinder pathFinder;
    private Path path;

    public Apple(Grid grid, LevelState levelState) {
        super(new Vector2(1.5f, 1.5f));
        this.grid = grid;
        this.levelState = levelState;
        this.pathFinder = new PathFinder(grid);

        ticks = 0;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public Grid getGrid() {
        return grid;
    }

    public Vector2 getDesired() {
        return desired;
    }

    public Vector2 getSteering() {
        return steering;
    }

    @Override
    public void update(float timestep) {
        super.update(timestep);

        Vector2 me, vel;
        me = getPosition();
        vel = getVelocity();

        //me.x += 0.001f;
        //me.y += 0.001f;

        if (levelState.getCurrentPowerup() == Powerup.KillAll) {
            markForKill();
        } else {
            vel.add(getRegularSteering(ticks));
        }

        wallBounce(grid, me, vel);

        vel.mul(DAMPING);
        vel.add((float) Math.random() - 0.5f, (float) Math.random() - 0.5f);

        if (vel.len() > CHASE_SPEED)
            vel.nor().mul(CHASE_SPEED);

        ticks++;
    }

    private void wallBounce(Grid grid, Vector2 me, Vector2 vel) {
        Vector2 m1, m2;
        m1 = me.cpy().sub(getBounds().cpy().mul(.5f)).mul(1f / grid.getUnitSize()).add(.5f, .5f);
        m2 = me.cpy().add(getBounds().cpy().mul(.5f)).mul(1f / grid.getUnitSize()).add(.5f, .5f);

        Vector2 g1, g2;
        g1 = m1.cpy();
        g1.x = (int) g1.x;
        g1.y = (int) g1.y;

        g2 = g1.cpy().add(1f, 1f);

        Vector2 penetration = new Vector2();


        /*
         * G12 ------------ G22
         *  |                |
         *  |                |
         *  |                |
         *  |                |
         * G11 ------------ G21
         */

        // G11
        if (Grid.isWall(grid.get(g1.x, g1.y, Grid.GRID_WALL))) {
            Vector2 g11 = MathUtil.collideAABB(m1, m2, g1, g2);
            penetration.x = MathUtil.absMax(g11.x, penetration.x);
            penetration.y = MathUtil.absMax(g11.y, penetration.y);
        }

        // G12
        if (Grid.isWall(grid.get(g1.x, g2.y, Grid.GRID_WALL))) {
            Vector2 g12 = MathUtil.collideAABB(m1, m2,
                                               new Vector2(g1.x, g1.y + 1),
                                               new Vector2(g2.x, g2.y + 1));
            penetration.x = MathUtil.absMax(g12.x, penetration.x);
            penetration.y = MathUtil.absMax(g12.y, penetration.y);
        }

        // G21
        if (Grid.isWall(grid.get(g2.x, g1.y, Grid.GRID_WALL))) {
            Vector2 g21 = MathUtil.collideAABB(m1, m2,
                                               new Vector2(g1.x + 1, g1.y),
                                               new Vector2(g2.x + 1, g2.y));
            penetration.x = MathUtil.absMax(g21.x, penetration.x);
            penetration.y = MathUtil.absMax(g21.y, penetration.y);
        }

        // G22
        if (Grid.isWall(grid.get(g2.x, g2.y, Grid.GRID_WALL))) {
            Vector2 g22 = MathUtil.collideAABB(m1, m2,
                                               new Vector2(g1.x + 1, g1.y + 1),
                                               new Vector2(g2.x + 1, g2.y + 1));
            penetration.x = MathUtil.absMax(g22.x, penetration.x);
            penetration.y = MathUtil.absMax(g22.y, penetration.y);
        }

        if (Math.abs(penetration.x) < Math.abs(penetration.y))
            penetration.x = 0;
        else
            penetration.y = 0;

        me.sub(penetration);
        if (penetration.x != 0)
            vel.x += penetration.x * COLLISION_RESPONSE;
        else
            vel.y += penetration.y * COLLISION_RESPONSE;
    }

    public Path getPath() {
        return path;
    }

    private Vector2 getRegularSteering(int ticks) {
        Vector2 me = getPosition();
        Vector2 meGrid = grid.pointToGrid(me);

        Vector2 vel = getVelocity();

        Vector2 andy = level.getAndyAndroid().getPosition();
        Vector2 andyGrid = grid.pointToGrid(andy);

        steering = new Vector2();

        if (path == null || ticks % 10 == 0) {
            Node start = new Node((int) meGrid.x, (int) meGrid.y);
            Node end = new Node((int) andyGrid.x, (int) andyGrid.y);
            path = pathFinder.getPath(start, end);
        }

        Vector2 dest = null;
        float dx, dy;
        for (int i = 0; i < path.size(); i++) {
            dest = path.get(i).toVector2(grid).add(1, 1);
            dx = dest.x - me.x;
            dy = dest.y - me.y;
            float d = (float) Math.sqrt(dx * dx + dy * dy);

            // if the first node (usually on top of us) is too far away, use it
            if (i == 0)
                if (d > 10f)
                    break;
                else
                    continue;

            // if the current node is not too close, use it
            if (d > 0.1f)
                break;
        }
        if (dest == null) {
            desired = andy.cpy().sub(me).nor().mul(CHASE_SPEED);
            steering.add(desired.cpy().sub(vel));
        } else {
            desired = dest.sub(me).nor().mul(CHASE_SPEED);
            steering.add(desired.cpy().sub(vel));
        }

        steering.nor();

        if (steering.len()
                > 0.8f)
            steering.nor().mul(0.8f);

        return steering;
    }
}
