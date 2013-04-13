/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pacandroid.PacAndroidGame;
import pacandroid.gui.Button;
import pacandroid.gui.GUI;

/**
 *
 * @author lachlan
 */
public class MainMenuScreen extends AbstractScreen {

    private final GUI gui;

    public MainMenuScreen(PacAndroidGame game) {
        super(game);

        gui = new GUI(800, 600);
        gui.setBackground(new Texture(Gdx.files.classpath(
                "buttons/background.png")));

        Texture playUp, playDown;
        playUp = new Texture(Gdx.files.classpath("gui/play-up.png"));
        playDown = new Texture(Gdx.files.classpath("gui/play-down.png"));

        Button playButton = new Button(playUp, playDown);
        playButton.setCentre(400, 450);
        playButton.setActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getGame().play();
            }
        });
        gui.add(playButton);

        Texture exitUp, exitDown;
        exitUp = new Texture(Gdx.files.classpath("gui/exit-up.png"));
        exitDown = new Texture(Gdx.files.classpath("gui/exit-down.png"));
        Button exitButton = new Button(exitUp, exitDown);
        exitButton.setCentre(400, 150);
        exitButton.setActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Gdx.app.exit();
            }
        });
        gui.add(exitButton);

        gui.enable();
    }

    @Override
    public void resize(int width, int height) {
        gui.resize(width, height);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        gui.draw();
    }

    @Override
    public void dispose() {
        gui.disable();
        gui.dispose();
    }
}
