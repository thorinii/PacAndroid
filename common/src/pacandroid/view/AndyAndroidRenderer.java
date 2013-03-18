package pacandroid.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import pacandroid.model.AndyAndroid;
import pacandroid.model.Direction;
import pacandroid.model.Entity;
import pacandroid.model.GridLockedDynamicEntity;

public class AndyAndroidRenderer extends AnimatedEntityRenderer {

    public AndyAndroidRenderer() {
        super(14);
        setFrames(new Texture[]{
            new Texture(Gdx.files.classpath("640x/android/andy-1.png")),
            new Texture(Gdx.files.classpath("640x/android/andy-2.png")),
            new Texture(Gdx.files.classpath("640x/android/andy-3.png")),
            new Texture(Gdx.files.classpath("640x/android/andy-4.png"))
        });
    }

    @Override
    public void renderEntity(Entity entity, SpriteBatch batch,
            DefaultLevelRenderer r) {
        if (!(entity instanceof AndyAndroid))
            throw new IllegalArgumentException("Must Render only AndyAndroid");
        AndyAndroid andy = (AndyAndroid) entity;

        Vector2 bounds = entity.getBounds();
        float x1 = (entity.getPosition().x) * r.getPPUWidth()
                + r.getOffsetX();
        float y1 = (entity.getPosition().y) * r.getPPUHeight()
                + r.getOffsetY();

        float gUSize = .5f * ((GridLockedDynamicEntity) entity)
                .getGrid().getUnitSize();

        int width = (int) (bounds.x * gUSize * r.getPPUWidth());
        int height = (int) (bounds.y * gUSize * r.getPPUHeight());
        Direction dir = andy.getDirection();

        update();
        Texture texture = andy.getVelocity().len() == 0 ? getFrame(1) : getFrame();

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
                batch.draw(texture, x1, y1, width / 2, height / 2, width,
                           height, 1, 1, 90, 0, 0,
                           texture.getWidth(), texture.getHeight(),
                           false, false);
                break;
            case Down:
                batch.draw(texture, x1, y1, width / 2, height / 2, width,
                           height, 1, 1, -90, 0, 0,
                           texture.getWidth(), texture.getHeight(),
                           false, false);
                break;
        }
    }
}
