package pacandroid.model;

import com.badlogic.gdx.math.Vector2;
import pacandroid.exceptions.GameException;

public abstract class Entity {

    private final Vector2 position;
    private final Vector2 bounds;
    private boolean markedForKill;

    public Entity(Vector2 bounds) {
        this.position = new Vector2();
        this.bounds = bounds;
        this.markedForKill = false;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getBounds() {
        return bounds;
    }

    public boolean isMarkedForKill() {
        return markedForKill;
    }

    public void markForKill() {
        this.markedForKill = true;
    }

    public void update(float timestep) {
    }

    public void collideWith(Entity other) throws GameException {
    }
}
