package tinytowns.game;

import com.badlogic.gdx.Game;

import tinytowns.game.screens.MainMenuScreen;

public class TinyTowns extends Game {
	public void create() {
		setScreen(new MainMenuScreen(this));
	}
}
