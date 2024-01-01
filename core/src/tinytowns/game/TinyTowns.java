package tinytowns.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import tinytowns.game.screens.MainMenuScreen;

public class TinyTowns extends Game {
	public void create() {
		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();
		setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render();
	}
}
