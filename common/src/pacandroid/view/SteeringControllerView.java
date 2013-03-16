package pacandroid.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import pacandroid.controller.SteeringController;

public class SteeringControllerView {

    private final SteeringController steeringController;
    private Texture baseTexture;
    private Texture dragTexture;

    public SteeringControllerView(SteeringController steeringController) {
        this.steeringController = steeringController;

        baseTexture = new Texture(Gdx.files.classpath("steering/base.png"));
        dragTexture = new Texture(Gdx.files.classpath("steering/drag.png"));
    }

    public SteeringController getSteeringController() {
        return steeringController;
    }

    public void draw(SpriteBatch batch) {
        Vector2 root = steeringController.getRoot();
        batch.draw(baseTexture,
                   root.x - baseTexture.getWidth() / 2,
                   root.y - baseTexture.getHeight() / 2);

        if (steeringController.isDown()) {
            Vector2 pos = steeringController.getViewPosition();
            batch.draw(dragTexture,
                       root.x + pos.x - dragTexture.getWidth() / 2,
                       root.y + pos.y - dragTexture.getHeight() / 2);
        }
    }
}
