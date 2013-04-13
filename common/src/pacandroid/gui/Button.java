/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.awt.event.ActionListener;

/**
 *
 * @author lachlan
 */
public class Button extends Actor {

    private final Texture upTexture;
    private final Texture downTexture;
    private boolean clicking;
    private ActionListener actionListener;

    public Button(Texture upTexture) {
        this(upTexture, null);
    }

    public Button(Texture upTexture, Texture downTexture) {
        this.upTexture = upTexture;
        this.downTexture = downTexture;

        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y,
                    int pointer, int button) {
                clicking = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer,
                    int button) {
                clicking = false;
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (actionListener != null)
                    actionListener.actionPerformed(null);
            }
        });

        setWidth(upTexture.getWidth());
        setHeight(upTexture.getHeight());
    }

    public void setCentre(float x, float y) {
        super.setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        if (clicking && downTexture != null)
            batch.draw(downTexture,
                       getX(),
                       getY());
        else
            batch.draw(upTexture,
                       getX(),
                       getY());
    }
}
