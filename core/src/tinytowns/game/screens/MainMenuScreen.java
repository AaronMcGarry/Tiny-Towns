package tinytowns.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import tinytowns.game.TinyTowns;

public class MainMenuScreen extends MenuScreen {
	public MainMenuScreen(TinyTowns game) {
		super(game);

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		root.add(new Label("Tiny Towns", skin));

		root.row();
		TextButton multiplayerButton = new TextButton("Find or host a game", skin);
		multiplayerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new ConnectMenuScreen(game));
				dispose();
			}
		});
		root.add(multiplayerButton);

		root.row();
		TextButton singleplayerButton = new TextButton("Singleplayer", skin);
		singleplayerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
			}
		});

		root.row();
		TextButton quitButton = new TextButton("Quit game", skin);
		quitButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		root.add(quitButton);
	}
}
