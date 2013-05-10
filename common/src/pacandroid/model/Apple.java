package pacandroid.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import pacandroid.model.LevelState.Powerup;

public class Apple extends DynamicEntity {

    public static final int AI_TRACK = 0;
    public static final int AI_PATHFIND = 1;
    public static final float CHASE_SPEED = 9f;
    public static final float WANDER_SPEED = 9f;
    public static final int TICKS_IN_STATE = 4;
    private final float SPEED_COEFFICIENT;
    private final Grid grid;
    private Level level;
    private int ticks;
    private final LevelState levelState;
    private AppleAI intelligence;
    private Vector2 steering = new Vector2();

    public Apple(Grid grid, LevelState levelState) {
        super(new Vector2(1.5f, 1.5f));
        this.grid = grid;
        this.levelState = levelState;

        SPEED_COEFFICIENT = (float) Math.max(Math.random(), .7);

        ticks = TICKS_IN_STATE;
    }

    public void setIntelligence(int intelligence) {
        switch (intelligence) {
            case AI_TRACK:
            default:
                this.intelligence = new AppleTrackingAI();
                break;
            //case AI_PATHFIND:
            //    this.intelligence = new ApplePathFindingAI();
            //    break;
        }
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

    public Vector2 getSteering() {
        return steering;
    }

    @Override
    public void update(float timestep) {
        super.update(timestep);

        getPosition().x += 0.001f;
        getPosition().y += 0.001f;

//        if (levelState.getCurrentPowerup() == Powerup.Edible) {
//            Vector2 andy = level.getAndyAndroid().getPosition();
//            Vector2 me = getPosition();
//            Vector2 velocity = new Vector2();
//
//            float dx, dy;
//            dx = me.x - andy.x;
//            dy = me.y - andy.y;
//
//            velocity.x = Math.min(dx, CHASE_SPEED);
//            velocity.y = Math.min(dy, CHASE_SPEED);
//
//            if (dx == 0)
//                velocity.x = -CHASE_SPEED;
//            if (dy == 0)
//                velocity.y = -CHASE_SPEED;
//        } else
        if (levelState.getCurrentPowerup() == Powerup.KillAll) {
            markForKill();
        } else {
            intelligence.update(getVelocity(), ticks);
        }

        ticks++;
    }

    private abstract class AppleAI {

        public abstract void update(Vector2 vel, int ticks);
    }

    private class AppleTrackingAI extends AppleAI {

        @Override
        public void update(Vector2 vel, int ticks) {
            Vector2 andy = level.getAndyAndroid().getPosition();
            Vector2 me = getPosition();
            steering = new Vector2();

            //steering.add(wallRepel(getGrid(), me));

//            if (levelState.getCurrentPowerup() == LevelState.Powerup.Edible) {
//                // Flee
//                Vector2 desired = andy.cpy().sub(me).nor();
//                steering.sub(desired.sub(vel));
//            } else {
            // Chase
            Vector2 desired = andy.cpy().sub(me).nor().mul(CHASE_SPEED);
            steering.add(desired.sub(vel));
//            }

            steering.nor();
            if (steering.len() > 2)
                steering.nor().mul(2);

            vel.add(steering);

            if (vel.len() > CHASE_SPEED)
                vel.nor().mul(CHASE_SPEED);

            wallBounce(grid, me, vel);
            setVelocity(vel);
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


            /*
             * G12 ------------ G22
             *  |                |
             *  |                |
             *  |                |
             *  |                |
             * G11 ------------ G21
             */

            // G11
            if (Grid.isWall(grid.get(g1.x, g1.y))) {
                Vector2 penetration = collideAABB(m1, m2, g1, g2);
                me.sub(penetration);
            }

            // G12
            if (Grid.isWall(grid.get(g1.x, g2.y))) {
                Vector2 penetration = collideAABB(m1, m2,
                                                  new Vector2(g1.x, g1.y + 1),
                                                  new Vector2(g2.x, g2.y + 1));
                me.sub(penetration);
            }

            // G21
            if (Grid.isWall(grid.get(g2.x, g1.y))) {
                Vector2 penetration = collideAABB(m1, m2,
                                                  new Vector2(g1.x + 1, g1.y),
                                                  new Vector2(g2.x + 1, g2.y));
                me.sub(penetration);
            }

            // G22
            if (Grid.isWall(grid.get(g2.x, g2.y))) {
                Vector2 penetration = collideAABB(m1, m2,
                                                  new Vector2(g1.x + 1, g1.y + 1),
                                                  new Vector2(g2.x + 1, g2.y + 1));
                me.sub(penetration);
            }
        }

        private Vector2 collideAABB(Vector2 m1, Vector2 m2, Vector2 g1, Vector2 g2) {
            Vector2 mc = new Vector2((m1.x + m2.x) / 2, (m1.y + m2.y) / 2);
            Vector2 gc = new Vector2((g1.x + g2.x) / 2, (g1.y + g2.y) / 2);

            Vector2 t = gc.cpy().sub(mc);

            float aXExtent = (m2.x - m1.x) / 2;
            float bXExtent = (g2.x - g1.x) / 2;
            float xOverlap = aXExtent + bXExtent - Math.abs(t.x);

            float aYExtent = (m2.y - m1.y) / 2;
            float bYExtent = (g2.y - g1.y) / 2;
            float yOverlap = aYExtent + bYExtent - Math.abs(t.y);

            if (xOverlap < yOverlap) {
                return new Vector2((t.x < 0) ? -Math.max(0, xOverlap) : Math.max(0, xOverlap), 0);
            } else {
                return new Vector2(0, (t.y < 0) ? -Math.max(0, yOverlap) : Math.max(0, yOverlap));
            }
        }
    }

    private class ApplePathFindingAI extends AppleAI {

        private List<Node> path;
        private Node target;

        @Override
        public void update(Vector2 vel, int ticks) {
            if (path == null || ticks % 10 == 0) {
                recalculatePath();
            }

            Vector2 me = getPosition();
            Vector2 node;
            Vector2 velocity = new Vector2();

            float dx = 0, dy = 0;

            while (path.size() > 0) {
                node = path.get(0).toVector2();
                dx = node.x - me.x;
                dy = node.y - me.y;

                if (Math.abs(dx) < 0.5 && Math.abs(dy) < 0.5) {
                    path.remove(0);
                } else {
                    break;
                }
            }

            if (Math.abs(dx) > 0.2) {
                velocity.x = Math.copySign(CHASE_SPEED * SPEED_COEFFICIENT, dx);
            } else if (Math.abs(dy) > 0.2) {
                velocity.y = Math.copySign(CHASE_SPEED * SPEED_COEFFICIENT, dy);
            }

            velocity.x += (float) Math.random() - 0.5f;
            velocity.y += (float) Math.random() - 0.5f;

            setVelocity(velocity);
        }

        public void recalculatePath() {
            path = new ArrayList<Node>();
            target = targetNode();

            Grid g = level.getGrid();
            PriorityQueue<Node> openSet = new PriorityQueue<Node>();
            Set<Node> closed = new HashSet<Node>();

            Node start = new Node(getPosition());
            openSet.add(start);

            while (!closed.contains(target)) {
                Node current = openSet.poll();
                if (current == null)
                    break;
                if (!closed.add(current))
                    continue;

                // Add all available to the openSet
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if ((i == 1 || i == -1) && (j == 1 || j == -1))
                            continue;
                        if (current.x + i < 0
                                || current.x + i >= g.getWidth()
                                || current.y + j < 0
                                || current.y + j >= g.getHeight())
                            continue;

                        if (!Grid.isWall(g.get(current.x + i, current.y + j))) {
                            Node n = new Node(current.x + i, current.y + j);
                            if (!available(n))
                                continue;

                            if (openSet.contains(n)) {
                                for (Node c : openSet) {
                                    if (c.equals(n)
                                            && c.dist(current) > n.dist(current)) {
                                        c.parent = current;
                                        break;
                                    }
                                }

                            } else {
                                n.parent = current;
                                openSet.add(n);
                            }
                        }
                    }
                }
            }

            for (Node c : closed) {
                if (c.equals(target)) {
                    Node current = c;

                    while (current != null && current.parent != null) {
                        path.add(current);
                        current = current.parent;
                    }

                    break;
                }
            }

            Collections.reverse(path);
        }

        private Node targetNode() {
            return new Node(level.getAndyAndroid().getPosition());
        }

        private boolean available(Node n) {
            if (levelState.getCurrentPowerup() == Powerup.Edible) {
                return !n
                        .equals(new Node(level.getAndyAndroid().getPosition()));
            } else {
                return true;
            }
        }

        private class Node implements Comparable<Node> {

            final int x, y;
            Node parent;

            public Node(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public Node(Vector2 vec) {
                x = (int) vec.x / Level.GRID_UNIT_SIZE;
                y = (int) vec.y / Level.GRID_UNIT_SIZE;
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + x;
                result = prime * result + y;
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                Node other = (Node) obj;
                if (x != other.x)
                    return false;
                if (y != other.y)
                    return false;
                return true;
            }

            public int dist(Node o) {
                return (int) Math.hypot(target.x - x, target.y - y);
            }

            @Override
            public int compareTo(Node o) {
                return dist(target)
                        - o.dist(target);
            }

            private ApplePathFindingAI getOuterType() {
                return ApplePathFindingAI.this;
            }

            public Vector2 toVector2() {
                Vector2 vec = new Vector2();
                vec.x = x * Level.GRID_UNIT_SIZE;
                vec.y = y * Level.GRID_UNIT_SIZE;
                return vec;
            }

            @Override
            public String toString() {
                return "Node [x=" + x + ", y=" + y + "]";
            }
        }
    }
}
