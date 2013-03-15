package pacandroid.controller;

import com.badlogic.gdx.math.Vector2;
import pacandroid.model.AndyAndroid;
import pacandroid.model.Level;
import pacandroid.screens.GameScreen;
import pacandroid.util.AppLog;

public class OldSteeringController {

    private Level level;
    private LevelController controller;
    private Vector2 screenSize;

    public OldSteeringController(Level world, LevelController controller,
            Vector2 screenSize) {
        this.level = world;
        this.controller = controller;
        this.screenSize = screenSize;
    }

    public void touchDown(int x, int y) {
        doTouchDown(new Vector2(x, y));
    }

    public void touchUp(int x, int y) {
        doTouchUp(new Vector2(x, y));
    }

    private void doTouchDown(Vector2 mouse) {
        mouse.y = (int) screenSize.y - mouse.y;
        mouse.x = mouse.x / GameScreen.GRID_UNIT * 2;
        mouse.y = mouse.y / GameScreen.GRID_UNIT * 2;

        mouse.x -= level.getGridUnitSize() / 2;
        mouse.y -= level.getGridUnitSize() / 2;

        AndyAndroid android = level.getAndyAndroid();

        Vector2 relative = mouse.cpy().add(android.getPosition().tmp().mul(-1));

        if (relative.y > 0) {
            if (relative.y > Math.abs(relative.x)) {
                AppLog.l("Up ");
                controller.upPressed();
            } else if (relative.x > 0) {
                AppLog.l("Right ");
                controller.rightPressed();
            } else {
                AppLog.l("Left ");
                controller.leftPressed();
            }
        } else {
            if (-relative.y > Math.abs(relative.x)) {
                controller.downPressed();
            } else if (relative.x > 0) {
                controller.rightPressed();
            } else {
                controller.leftPressed();
            }
        }
    }

    private void doTouchUp(Vector2 mouse) {
        controller.leftReleased();
        controller.rightReleased();
        controller.upReleased();
        controller.downReleased();
    }
}
