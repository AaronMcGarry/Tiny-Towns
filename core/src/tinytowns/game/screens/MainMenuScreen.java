package tinytowns.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen; 
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import tinytowns.game.TinyTowns;

public class MainMenuScreen implements Screen {
	private Stage stage;
	private TinyTowns game;

	public MainMenuScreen(TinyTowns game) {
		this.game = game;

		Skin skin = new Skin(Gdx.files.internal("holoui/Holo-light-ldpi.json"));

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		root.add(new Label("Tiny Towns", skin));

		root.row();
		TextButton multiplayerButton = new TextButton("Find or host a game", skin);
		multiplayerButton.addListener(new MultiplayerButtonListener());
		root.add(multiplayerButton);

		root.row();
		TextButton quitButton = new TextButton("Quit game", skin);
		quitButton.addListener(new QuitButtonListener());
		root.add(quitButton);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }

	private class MultiplayerButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			game.setScreen(new ConnectScreen(game));
			dispose();
		}
	}

	private class QuitButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			Gdx.app.exit();
		}
	}
}
