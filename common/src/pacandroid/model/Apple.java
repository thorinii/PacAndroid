package pacandroid.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import pacandroid.model.LevelState.Powerup;

public class Apple extends GridLockedDynamicEntity {

    public static final int AI_WANDER = 0;
    public static final int AI_TRACK = 1;
    public static final int AI_PATHFIND = 2;
    public static final float CHASE_SPEED = 9f;
    public static final float WANDER_SPEED = 9f;
    public static final int TICKS_IN_STATE = 4;
    private final float SPEED_COEFFICIENT;
    private Level level;
    private int ticks;
    private final LevelState levelState;
    private AppleAI intelligence;

    public Apple(Grid grid, LevelState levelState) {
        super(new Vector2(1.875f, 1.875f), grid);
        this.levelState = levelState;

        SPEED_COEFFICIENT = (float) Math.max(Math.random(), .7);

        ticks = TICKS_IN_STATE;
    }

    public void setIntelligence(int intelligence) {
        switch (intelligence) {
            case AI_WANDER:
                this.intelligence = new AppleWanderAI();
                break;
            case AI_TRACK:
                this.intelligence = new AppleTrackingAI();
                break;
            case AI_PATHFIND:
                this.intelligence = new ApplePathFindingAI();
                break;
        }
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    protected boolean handleGridSpace(int x, int y, int gridSpace) {
        return false;
    }

    @Override
    public void update(float timestep) {
        super.update(timestep);

        if (levelState.getCurrentPowerup() == Powerup.Edible) {
            Vector2 andy = level.getAndyAndroid().getPosition();
            Vector2 me = getPosition();
            Vector2 velocity = new Vector2();

            float dx, dy;
            dx = me.x - andy.x;
            dy = me.y - andy.y;

            velocity.x = Math.min(dx, CHASE_SPEED);
            velocity.y = Math.min(dy, CHASE_SPEED);

            if (dx == 0)
                velocity.x = -CHASE_SPEED;
            if (dy == 0)
                velocity.y = -CHASE_SPEED;
        } else if (levelState.getCurrentPowerup() == Powerup.KillAll) {
            markForKill();
        } else {
            intelligence.update(getVelocity(), ticks);
        }

        ticks++;
    }

    private abstract class AppleAI {

        public abstract void update(Vector2 vel, int ticks);
    }

    private class AppleWanderAI extends AppleAI {

        @Override
        public void update(Vector2 vel, int ticks) {
            wander(vel, ticks);
        }

        private void wander(Vector2 oldVel, int ticks) {
            if (ticks % 10 == 0) {
                Vector2 velocity = new Vector2();

                velocity.x = WANDER_SPEED * SPEED_COEFFICIENT
                        * (float) (Math.random() - 0.5f);

                velocity.y = WANDER_SPEED * SPEED_COEFFICIENT
                        * (float) (Math.random() - 0.5f);


                if ((oldVel.x < 0 && velocity.x > 0) || (oldVel.x > 0
                        && velocity.x < 0))
                    velocity.x = -velocity.x;
                if ((oldVel.y < 0
                        && velocity.y > 0) || (oldVel.y > 0 && velocity.y < 0))
                    velocity.y = -velocity.y;

                setVelocity(velocity);
            }
        }
    }

    private class AppleTrackingAI extends AppleAI {

        @Override
        public void update(Vector2 vel, int ticks) {
            if (ticks % (int) (Math.random() * 2 * TICKS_IN_STATE + 1) < TICKS_IN_STATE)
                wander(vel, ticks);
            else
                chase(ticks);
        }

        private void wander(Vector2 oldVel, int ticks) {
            if (ticks % 5 == 0) {
                Vector2 velocity = new Vector2();

                velocity.x = (float) Math
                        .copySign(WANDER_SPEED * SPEED_COEFFICIENT,
                                  Math.random() - 0.5);

                velocity.y = (float) Math
                        .copySign(WANDER_SPEED * SPEED_COEFFICIENT,
                                  Math.random() - 0.5);

                if (oldVel.x < 0 && velocity.x > 0)
                    velocity.x *= -1;
                else if (oldVel.x > 0 && velocity.x < 0)
                    velocity.x *= -1;
                if (oldVel.y < 0 && velocity.y > 0)
                    velocity.y *= -1;
                else if (oldVel.y > 0 && velocity.y < 0)
                    velocity.y *= -1;

                setVelocity(velocity);
            }
        }

        private void chase(int ticks) {
            Vector2 andy = level.getAndyAndroid().getPosition();
            Vector2 me = getPosition();
            Vector2 velocity = new Vector2();

            float dx, dy;

            if (levelState.getCurrentPowerup() == LevelState.Powerup.Edible) {
                // Flee
                dx = me.x - andy.x;
                dy = me.y - andy.y;
            } else {
                // Chase
                dx = andy.x - me.x;
                dy = andy.y - me.y;
            }

            if (Math.abs(dx) > Math.abs(dy)) {
                velocity.x = Math.copySign(CHASE_SPEED * SPEED_COEFFICIENT,
                                           dx);
                velocity.y = 0;

                setVelocity(velocity);
            } else {
                velocity.x = 0;
                velocity.y = Math.copySign(CHASE_SPEED * SPEED_COEFFICIENT,
                                           dy);

                setVelocity(velocity);
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
