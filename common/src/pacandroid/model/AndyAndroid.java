package pacandroid.model;

import com.badlogic.gdx.math.Vector2;
import pacandroid.model.LevelState.Powerup;

public class AndyAndroid extends GridLockedDynamicEntity {

    public static final float REGULAR_SPEED = 10f;
    private final LevelState levelState;

    public AndyAndroid(Grid grid, LevelState levelState) {
        super(new Vector2(1.875f, 1.875f), grid);
        this.levelState = levelState;
    }

    public LevelState getLevelState() {
        return levelState;
    }

    @Override
    protected boolean handleGridSpace(int x, int y, int gridSpace) {
        if (gridSpace == Grid.GRID_JELLYBEAN) {
            levelState.getScore().eatJellyBean();
            return true;
        } else if (gridSpace == Grid.GRID_POWERUP) {
            levelState.getScore().eatPowerup();
            levelState.choosePowerup(x, y);
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
        levelState.setGameOver(true);
    }

    @Override
    public void collideWith(Entity other) {
        if (other instanceof Apple) {
            if (levelState.getCurrentPowerup() == Powerup.Edible) {
                other.markForKill();
                levelState.getScore().eatApple();
            } else {
                levelState.takeLife();
                markForKill();
                other.markForKill();
            }
        }
    }
}
