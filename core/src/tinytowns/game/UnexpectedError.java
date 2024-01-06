package tinytowns.game;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.alexdlaird.ngrok.NgrokClient;

public class UnexpectedError extends ApplicationAdapter {
    public Exception exception;
    public NgrokClient client;
    private Stage stage;

    @Override
    public void create() {
        if (client != null)
            client.kill();
        
        Gdx.graphics.setContinuousRendering(false);
        Gdx.graphics.requestRendering();

        int i = 1;
        for (; Gdx.files.external("TinyTownsErrorLogs/log" + i + ".txt").exists(); i++) {}
        FileHandle file = Gdx.files.external("TinyTownsErrorLogs/log" + i + ".txt");
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        file.writeString(stackTrace.toString(), false);

        String fileSeparator = System.getProperty("os.name").contains("Windows") ? "\\" : "/";
        Skin skin = new Skin(Gdx.files.internal("holoui-dark/Holo-dark-ldpi.json"));
        stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
        Label message = new Label("An unexpected error occurred.\nOpen the file at " + Gdx.files.getExternalStoragePath() + "TinyTownsErrorLogs" + fileSeparator + "log" + i + ".txt for more information.", skin);
        message.setAlignment(Align.center);
        root.add(message);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
