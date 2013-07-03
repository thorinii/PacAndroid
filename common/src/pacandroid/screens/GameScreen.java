/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacandroid.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import pacandroid.AppLog;
import pacandroid.controller.LevelController;
import pacandroid.controller.SteeringController;
import pacandroid.view.DefaultLevelRenderer;
import pacandroid.view.LevelRenderer;
import pacandroid.model.Level;
import pacandroid.PacAndroidGame;
import pacandroid.stats.HeatMap;
import pacandroid.view.fonts.FontRenderer;

/**
 *
 * @author lachlan
 */
public class GameScreen extends AbstractScreen {

    /**
     * The size of 1 grid square (or 2 units): 32px
     */
    public static final int GRID_UNIT = 55;
    public static final float MIN_DELTA = 0.015f;
    public static final float REGULAR_DELTA = 0.015f;
    private final Level level;
    private LevelController controller;
    private LevelRenderer[] renderers;
    private SteeringController steeringController;
    private final FontRenderer fontRenderer;
    //
    private float lastSmallDelta;

    public GameScreen(PacAndroidGame game, Level level, FontRenderer fontRenderer) {
        super(game);
        this.level = level;
        this.fontRenderer = fontRenderer;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (delta >= MIN_DELTA || lastSmallDelta >= REGULAR_DELTA) {
            updateLevel(REGULAR_DELTA);
            lastSmallDelta = 0;
        } else {
            updateLevel(MIN_DELTA);
            lastSmallDelta += delta;
        }

        renderLevel(REGULAR_DELTA);
    }

    private void updateLevel(float delta) {
        try {
            level.removeDead();

            if (!level.isGameOver())
                level.update(delta);
            controller.update(delta);
        } catch (Exception e) {
            AppLog.l("Error: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void renderLevel(float delta) {
        for (LevelRenderer renderer : renderers)
            renderer.render(delta);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new InputHandler());

        controller = new LevelController(level);


        steeringController = new SteeringController(level, controller,
                                                    getScreenSize());
        steeringController.setRoot(new Vector2(150, 150));


        DefaultLevelRenderer renderer = new DefaultLevelRenderer(
                (int) getScreenSize().x, (int) getScreenSize().y,
                level, fontRenderer);
        renderer.setSteeringController(steeringController);

        renderers = new LevelRenderer[]{
            renderer, //new DebugWorldRenderer(false, level)
        };

    }

    class InputHandler implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Keys.LEFT:
                    controller.leftPressed();
                    break;
                case Keys.RIGHT:
                    controller.rightPressed();
                    break;
                case Keys.UP:
                    controller.upPressed();
                    break;
                case Keys.DOWN:
                    controller.downPressed();
                    break;
            }
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Keys.LEFT:
                    controller.leftReleased();
                    break;
                case Keys.RIGHT:
                    controller.rightReleased();
                    break;
                case Keys.UP:
                    controller.upReleased();
                    break;
                case Keys.DOWN:
                    controller.downReleased();
                    break;
            }
            return true;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer,
                int button) {
            steeringController.touchDown(screenX, screenY);
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            steeringController.touchUp(screenX, screenY);

            if (level.isGameOver()) {
                writeHeatmap();
                gotoScreen(new GameOverScreen(
                        getGame(), fontRenderer,
                        level.getTimeOnLevel(), level.getScore()));
            }

            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            steeringController.touchDragged(screenX, screenY);
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return true;
        }

        @Override
        public boolean scrolled(int amount) {
            return true;
        }
    }

    private void writeHeatmap() {
        writeHeatmapToHTTP();

        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            try {
                writeHeatmapToFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void writeHeatmapToHTTP() {
        final String type = Gdx.app.getType().name().toLowerCase();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL dest = new URL("http://www.terrifictales.net/pa/stat-heatmap.php"
                            + "?kqwu=aSD8dh2s09d2"
                            + "&level=" + level.getName()
                            + "&client=" + type);
                    HttpURLConnection connection = (HttpURLConnection) dest.openConnection();

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content/Type", "application/octet-stream");
                    connection.setRequestMethod("POST");

                    writeHeatmap(connection.getOutputStream(), level.getHeatMap());

                    InputStream is = connection.getInputStream();
                    byte[] buf = new byte[1024];
                    System.out.println("Reading (" + connection.getResponseCode() + ")");
                    while (is.read(buf) != -1) ;
                    is.close();



                    dest = new URL("http://www.terrifictales.net/pa/stat-deathmap.php"
                            + "?akeu=d83hs7uJsjeSufdk"
                            + "&level=" + level.getName()
                            + "&client=" + type);
                    connection = (HttpURLConnection) dest.openConnection();

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content/Type", "application/octet-stream");
                    connection.setRequestMethod("POST");

                    writeHeatmap(connection.getOutputStream(), level.getDeathMap());

                    is = connection.getInputStream();
                    System.out.println("Reading (" + connection.getResponseCode() + ")");
                    while (is.read(buf) != -1) ;
                    is.close();

                    System.out.println("Uploaded data");
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }).start();
    }

    private void writeHeatmapToFile() throws IOException {
        if (!Gdx.files.isExternalStorageAvailable())
            return;
        FileHandle fh = Gdx.files.external(".pacandroid/stats/");
        if (!fh.exists())
            fh.mkdirs();

        String uuid = UUID.randomUUID().toString();
        try {
            writeHeatmap(
                    fh.child("heatmap-" + uuid + ".dat").write(false),
                    level.getHeatMap());
            writeHeatmap(
                    fh.child("heatmap-" + uuid + ".dat").write(false),
                    level.getDeathMap());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void writeHeatmap(OutputStream heatmap, HeatMap map) throws IOException {
        DataOutputStream dos = new DataOutputStream(heatmap);
        try {
            map.writeOut(dos, level.getGrid());
            dos.flush();
        } finally {
            dos.close();
        }
    }
}
