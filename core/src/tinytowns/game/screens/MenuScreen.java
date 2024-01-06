package tinytowns.game.screens;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectIntMap.Entry;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import tinytowns.game.TinyTowns;

public abstract class MenuScreen extends AbstractScreen {
    protected Skin skin;
    protected Stage stage;

    private Array<Texture> buildings;
    private ObjectIntMap<Rectangle> spawnedBuildings;
    private OrthographicCamera camera;
    private float sinceLastSpawn;
    private SpriteBatch batch;
    private java.util.Random r;
    private float rotation;

    public MenuScreen(TinyTowns game) {
        super(game);

        skin = new Skin(Gdx.files.internal("holoui-light/Holo-light-ldpi.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Gdx.graphics.setContinuousRendering(true);
        buildings = new Array<>();
        buildings.add(new Texture("sprites/buildings/blue.png"));
        buildings.add(new Texture("sprites/buildings/gray.png"));
        buildings.add(new Texture("sprites/buildings/green.png"));
        buildings.add(new Texture("sprites/buildings/orange.png"));
        buildings.add(new Texture("sprites/buildings/pink.png"));
        buildings.add(new Texture("sprites/buildings/purple.png"));
        buildings.add(new Texture("sprites/buildings/red.png"));
        buildings.add(new Texture("sprites/buildings/yellow.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        sinceLastSpawn = 0;
        spawnedBuildings = new ObjectIntMap<>();
        batch = new SpriteBatch();
        r = new Random();
    }

    @Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Iterator<Entry<Rectangle>> it = spawnedBuildings.entries();
        while (it.hasNext()) {
            Entry<Rectangle> rect = it.next();
            if (rect.key.x > Gdx.graphics.getWidth() + 200)
                it.remove();
        }
            

        sinceLastSpawn += delta;
        if (sinceLastSpawn >= 1) {
            int rand = r.nextInt(buildings.size);
            Texture texture = buildings.get(rand);
            spawnedBuildings.put(new Rectangle(-100, r.nextInt(50, Gdx.graphics.getHeight()-100), texture.getWidth() , texture.getHeight()), rand);
            sinceLastSpawn--;
        }

        for (Entry<Rectangle> rect : spawnedBuildings.entries())
            rect.key.x += 120 * delta;
        rotation += 30 * delta;

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Entry<Rectangle> rect : spawnedBuildings.entries())
            batch.draw(
                buildings.get(rect.value),
                rect.key.x,
                rect.key.y,
                rect.key.getWidth()/2,
                rect.key.getHeight()/2,
                rect.key.getWidth(),
                rect.key.getHeight(),
                1f,
                1f,
                rotation,
                0,
                0,
                (int)rect.key.getWidth(),
                (int)rect.key.getHeight(),
                false,
                false
            );
        batch.end();
        
		stage.act();
		stage.draw();
	}

    @Override
    public void dispose() {
        stage.dispose();
		skin.dispose();
        for (Texture building : buildings)
            building.dispose();
        batch.dispose();
    }
}