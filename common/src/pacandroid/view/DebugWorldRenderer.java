package pacandroid.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import pacandroid.model.Entity;
import pacandroid.model.Grid;
import pacandroid.model.Level;

public class DebugWorldRenderer implements LevelRenderer {

    private OrthographicCamera cam;
    private ShapeRenderer debugRenderer;
    private Color entityColour;
    private Color posColour;
    private Color emptyColour;
    private Color wallColour;
    private Color jellyBeanColour;
    private boolean renderWalls;
    private Level world;

    public DebugWorldRenderer(boolean renderWalls, Level world) {
        this.cam = new OrthographicCamera(world.getWorldWidth(), world
                .getWorldHeight());
        this.cam.position.set(world.getWorldWidth() / 2
                - world.getGridUnitSize() / 2,
                              world.getWorldHeight() / 2
                - world.getGridUnitSize() / 2,
                              0);
        this.cam.update();

        this.world = world;

        debugRenderer = new ShapeRenderer();

        this.renderWalls = renderWalls;
        entityColour = new Color(1, 0, 0, 1f);
        posColour = new Color(1, 1, 1, 1f);
        emptyColour = new Color(0, 0, 0, .5f);
        wallColour = new Color(0f, .8f, .8f, 1f);
        jellyBeanColour = new Color(0, 1, 0, 1f);
    }

    @Override
    public void render(float delta) {
        debugRenderer.setProjectionMatrix(cam.combined);

        if (renderWalls) {
            Grid grid = world.getGrid();
            int gUSize = grid.getUnitSize();
            int gHUSize = grid.getUnitSize() / 2;

            debugRenderer.begin(ShapeType.Line);
            debugRenderer.setColor(new Color(.1f, .1f, .1f, 1));
            for (int i = 0; i < grid.getWidth(); i++) {
                debugRenderer.line(i * gUSize - gHUSize, 0, i * gUSize
                        - gHUSize, grid.getHeight() * gUSize);
            }
            for (int j = 0; j < grid.getHeight(); j++) {
                debugRenderer.line(0, j * gUSize - gHUSize, grid.getWidth()
                        * gUSize, j * gUSize
                        - gHUSize);
            }
            debugRenderer.end();


            debugRenderer.begin(ShapeType.Rectangle);
            for (int i = 0; i < world.getGridWidth(); i++) {
                for (int j = 0; j < world.getGridHeight(); j++) {
                    if (Grid.isWall(grid.get(i, j))) {
                        debugRenderer.setColor(wallColour);
                    } else if (grid.get(i, j) == Grid.GRID_JELLYBEAN) {
                        debugRenderer.setColor(jellyBeanColour);
                    } else {
                        // debugRenderer.setColor(emptyColour);
                        continue;
                    }

                    debugRenderer.rect(i * gUSize - gHUSize, j * gUSize
                            - gHUSize, gUSize, gUSize);
                }
            }
            debugRenderer.end();
        }

        debugRenderer.begin(ShapeType.Rectangle);
        for (Entity entity : world.getEntities()) {
            Vector2 bounds = entity.getBounds();

            float x1 = entity.getPosition().x - bounds.x / 2;
            float y1 = entity.getPosition().y - bounds.y / 2;

            debugRenderer.setColor(entityColour);
            debugRenderer.rect(x1, y1, bounds.x, bounds.y);
        }
        debugRenderer.end();

        debugRenderer.begin(ShapeType.Line);
        for (Entity entity : world.getEntities()) {
            float x1 = entity.getPosition().x;
            float y1 = entity.getPosition().y;

            debugRenderer.setColor(posColour);
            debugRenderer.line(x1 - 2, y1, x1 + 2, y1);
            debugRenderer.line(x1, y1 - 2, x1, y1 + 2);
        }
        debugRenderer.end();
    }
}