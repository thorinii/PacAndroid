package pacandroid.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import pacandroid.model.Apple;
import pacandroid.model.Direction;
import pacandroid.model.Entity;
import pacandroid.model.GridLockedDynamicEntity;

public class AppleRenderer implements EntityRenderer {

    private Texture texture;

    public AppleRenderer() {
        texture = new Texture(Gdx.files
                .classpath("640x/apple-30x30.png"));
    }

    @Override
    public void renderEntity(Entity entity, SpriteBatch batch,
            DefaultLevelRenderer r) {
        if (!(entity instanceof Apple))
            throw new IllegalArgumentException("Must Render only Apple");

        Apple apple = (Apple) entity;

        Vector2 bounds = entity.getBounds();
        float x1 = (entity.getPosition().x) * r.getPPUWidth()
                + r.getOffsetX();
        float y1 = (entity.getPosition().y) * r.getPPUHeight()
                + r.getOffsetY();

        float gUSize = .5f * apple.getGrid().getUnitSize();

        int width = (int) (bounds.x * gUSize * r.getPPUWidth());
        int height = (int) (bounds.y * gUSize * r.getPPUHeight());
        Direction dir = apple.getDirection();

        switch (dir) {
            case Right:
                batch.draw(texture, x1, y1, width, height);
                break;
            case Left:
                batch.draw(texture, x1, y1, width, height, 0, 0,
                           texture.getWidth(), texture.getHeight(),
                           true, false);
                break;
            case Up:
                batch.
                        draw(texture, x1, y1, width / 2, height / 2, width,
                             height, 1, 1, 90, 0, 0,
                             texture.getWidth(), texture.getHeight(),
                             false, false);
                break;
            case Down:
                batch.
                        draw(texture, x1, y1, width / 2, height / 2, width,
                             height, 1, 1, -90, 0, 0,
                             texture.getWidth(), texture.getHeight(),
                             false, false);
                break;
        }
    }
}
