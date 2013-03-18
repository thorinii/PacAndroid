/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 *
 * @author lachlan
 */
public abstract class AnimatedEntityRenderer implements EntityRenderer {

    private final int tpf;
    private Texture[] frames;
    private long tickTime;

    public AnimatedEntityRenderer(int fps) {
        tpf = 1000 / fps;
        tickTime = 0;
    }

    protected void setFrames(Texture[] frames) {
        this.frames = frames;
    }

    protected void update() {
        tickTime += (long) (Gdx.graphics.getDeltaTime() * 1000f);
    }

    protected Texture getFrame() {
        return frames[(int) Math.round(tickTime / tpf) % frames.length];
    }

    protected Texture getFrame(int frame) {
        return frames[frame];
    }
}
