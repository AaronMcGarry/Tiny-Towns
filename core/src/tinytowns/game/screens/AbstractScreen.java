package tinytowns.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

import tinytowns.game.TinyTowns;

public abstract class AbstractScreen implements Screen {
    protected static TinyTowns game;
    protected static Array<String> players;

    public static void init(TinyTowns game) {
        AbstractScreen.game = game;
        players = new Array<>();
    }

	public void clientConnected(String name) {
        if (!players.contains(name, false))
            players.add(name);
        else {
            int i = 2;
            for (; players.contains(name + " " + i, false); i++) {}
            players.add(name + " " + i);
        }
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
}
