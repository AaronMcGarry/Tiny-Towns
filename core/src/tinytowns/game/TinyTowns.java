package tinytowns.game;

import com.badlogic.gdx.Game;
import com.github.alexdlaird.ngrok.NgrokClient;

import tinytowns.game.screens.MainMenuScreen;

public class TinyTowns extends Game {
	private UnexpectedError errorScreen;

	public TinyTowns(UnexpectedError errorScreen) {
		this.errorScreen = errorScreen;
	}

	public void setNgrok(NgrokClient client) {
		errorScreen.client = client;
	}

	public void killNgrok() {
		if (errorScreen.client != null)
			errorScreen.client.kill();
	}

	@Override
	public void create() {
		errorScreen.client = null;
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void dispose() {
		killNgrok();
	}
}
