package tinytowns.game.screens;

import com.badlogic.gdx.Screen;

import tinytowns.game.TinyTowns;

public abstract class AbstractScreen implements Screen {
    protected TinyTowns game;

    public AbstractScreen(TinyTowns game) {
        this.game = game;
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
