package pacandroid.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pacandroid.model.Entity;

public interface EntityRenderer {

    public void renderEntity(Entity entity, SpriteBatch batch,
            DefaultLevelRenderer renderer);
}
