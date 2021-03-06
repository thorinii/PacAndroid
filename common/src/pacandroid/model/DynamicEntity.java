package pacandroid.model;

import com.badlogic.gdx.math.Vector2;

public abstract class DynamicEntity extends Entity {

    private final Vector2 velocity;

    public DynamicEntity(Vector2 bounds) {
        super(bounds);

        this.velocity = new Vector2();
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void update(float timestep) {
        Vector2 pos = getPosition();
        pos.add(velocity.tmp().mul(timestep));
    }

    public Direction getDirection() {
        if (velocity.y > 0) {
            return Direction.Up;
        } else if (velocity.y < 0) {
            return Direction.Down;
        } else if (velocity.x < 0) {
            return Direction.Left;
        } else {
            return Direction.Right;
        }
    }
}
