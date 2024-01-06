package tinytowns.game;

import com.badlogic.gdx.Game;
import com.github.alexdlaird.ngrok.NgrokClient;

import tinytowns.game.screens.MainMenuScreen;

public class TinyTowns extends Game {
	private NgrokClient client;

	public void setNgrokClient(NgrokClient client) {
		this.client = client;
	}

	@Override
	public void create() {
		client = null;
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void dispose() {
		if (client != null)
			client.kill();
	}
}
