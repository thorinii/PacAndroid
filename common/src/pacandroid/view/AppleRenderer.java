package pacandroid.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import pacandroid.model.Apple;
import pacandroid.model.Entity;

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

        float x1 = (entity.getPosition().x) * r.getPPUWidth()
                + r.getOffsetX();
        float y1 = (entity.getPosition().y) * r.getPPUHeight()
                + r.getOffsetY();

        float gUSize = .5f * apple.getGrid().getUnitSize();

        x1 += 0.15f * gUSize * r.getPPUHeight();
        y1 += 0.15f * gUSize * r.getPPUHeight();

        int width = (int) (1.5 * gUSize * r.getPPUWidth());
        int height = (int) (1.5 * gUSize * r.getPPUHeight());
        Vector2 direction = apple.getDesired();


        batch.draw(texture, x1, y1, width / 2, height / 2, width,
                   height, 1, 1, direction.angle(), 0, 0,
                   texture.getWidth(), texture.getHeight(),
                   false, false);
    }
}
