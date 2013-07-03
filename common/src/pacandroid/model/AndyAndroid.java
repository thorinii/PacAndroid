package pacandroid.model;

import com.badlogic.gdx.math.Vector2;
import pacandroid.model.Powerup;

public class AndyAndroid extends GridLockedDynamicEntity {

    public static float REGULAR_SPEED = 6f;
    private final Level level;

    public AndyAndroid(Grid grid, Level level) {
        super(new Vector2(1.875f, 1.875f), grid);
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    protected boolean handleGridSpace(int x, int y, int gridSpace) {
        if (gridSpace == Grid.GRID_JELLYBEAN) {
            level.getScore().eatJellyBean();
            return true;
        } else if (gridSpace == Grid.GRID_POWERUP) {
            level.getScore().eatPowerup();
            level.choosePowerup(x, y);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void update(float timestep) {
        super.update(timestep);

        checkIfAllEaten();
    }

    private void checkIfAllEaten() {
        Grid g = getGrid();

        for (int x = 0; x < g.getWidth(); x++) {
            for (int y = 0; y < g.getHeight(); y++) {
                if (g.get(x, y) == Grid.GRID_JELLYBEAN) {
                    return;
                }
            }
        }

        // They are all gone
        level.setGameOver(true);
    }

    @Override
    public void collideWith(Entity other) {
        if (other instanceof Apple) {
            if (level.getCurrentPowerup() == Powerup.Edible) {
                other.markForKill();
                level.getScore().eatApple();
            } else {
                level.takeLife();
                markForKill();
                other.markForKill();
            }
        }
    }
}
