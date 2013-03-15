package pacandroid.controller;

import com.badlogic.gdx.math.Vector2;
import pacandroid.model.Level;

public class SteeringController {

    private static final float MAX = 100;
    private static final float MIN = 20;
    private final Level level;
    private final LevelController controller;
    private final Vector2 screenSize;
    private final Vector2 pos = new Vector2();
    private Vector2 root;
    private boolean down;

    public SteeringController(Level world, LevelController controller,
            Vector2 screenSize) {
        this.level = world;
        this.controller = controller;
        this.screenSize = screenSize;
    }

    public void setRoot(Vector2 root) {
        this.root = root;
    }

    public Vector2 getRoot() {
        return root;
    }

    public void touchDown(int x, int y) {
        doTouchDown(new Vector2(x, y));
    }

    public void touchUp(int x, int y) {
        doTouchUp(new Vector2(x, y));
    }

    public void touchDragged(int x, int y) {
        doTouchDragged(new Vector2(x, y));
    }

    public boolean isDown() {
        return down;
    }

    public Vector2 getViewPosition() {
        return pos.cpy().mul(MAX);
    }

    public Vector2 getPosition() {
        return pos;
    }

    private void doTouchDown(Vector2 mouse) {
        down = true;

        move(mouse);
    }

    private void doTouchUp(Vector2 mouse) {
        down = false;

        pos.set(Vector2.Zero);
        controller.setTouchControl(pos);
    }

    private void doTouchDragged(Vector2 mouse) {
        move(mouse);
    }

    private void move(Vector2 mouse) {
        mouse.y = (int) screenSize.y - mouse.y;

        pos.x = mouse.x - root.x;
        pos.y = mouse.y - root.y;

        normaliseVector(pos);

        controller.setTouchControl(pos);
    }

    private void normaliseVector(Vector2 steering) {
        if (steering.len() > MAX) {
            steering.nor().mul(MAX);
        }
        if (steering.len() < MIN && steering.len() != 0) {
            steering.nor().mul(0);
        }

        if (Math.abs(steering.x) > Math.abs(steering.y)) {
            steering.x = steering.x / Math.abs(steering.x);
            steering.y = 0;
        } else if (Math.abs(steering.x) < Math.abs(steering.y)) {
            steering.x = 0;
            steering.y = steering.y / Math.abs(steering.y);
        }
    }
}
