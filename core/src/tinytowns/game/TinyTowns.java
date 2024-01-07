package tinytowns.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.github.alexdlaird.ngrok.NgrokClient;

import tinytowns.game.screens.AbstractScreen;
import tinytowns.game.screens.MainMenuScreen;

public class TinyTowns extends Game {
	private UnexpectedError errorScreen;
	private AbstractScreen currentScreen;

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

	public void setScreen(AbstractScreen screen) {
		setScreen((Screen)screen);
		currentScreen = screen;
	}

	public AbstractScreen getScreen() {
		return currentScreen;
	}

	@Override
	public void create() {
		errorScreen.client = null;
		AbstractScreen.init(this);
		setScreen(new MainMenuScreen());
	}

	@Override
	public void dispose() {
		killNgrok();
	}
}
