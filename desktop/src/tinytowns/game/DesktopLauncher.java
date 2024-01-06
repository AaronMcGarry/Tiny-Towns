package tinytowns.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration gameConfig = new Lwjgl3ApplicationConfiguration();
		gameConfig.setTitle("Tiny Towns");
		gameConfig.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		gameConfig.useVsync(true);
		gameConfig.setForegroundFPS(60);
		UnexpectedError errorScreen = new UnexpectedError();
		try {
    		new Lwjgl3Application(new TinyTowns(errorScreen), gameConfig);
		} catch (Exception e) {
			Lwjgl3ApplicationConfiguration errorConfig = new Lwjgl3ApplicationConfiguration();
			errorConfig.setTitle("Error!");
			errorConfig.setWindowedMode(1280, 720);
			errorConfig.useVsync(true);
			errorConfig.setForegroundFPS(60);
			errorScreen.exception = e;
			new Lwjgl3Application(errorScreen, errorConfig);
		}
	}
}
