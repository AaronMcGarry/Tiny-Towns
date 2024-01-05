package tinytowns.game.screens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.alexdlaird.exception.NgrokException;
import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;

import tinytowns.game.TinyTowns;

public class ConnectMenuScreen extends AbstractScreen {
	private Skin skin;

	private Stage stage;
	private Table root;
	private TextField tokenField;
	private TextField urlField;

	public ConnectMenuScreen(TinyTowns game) {
		super(game);

		skin = new Skin(Gdx.files.internal("holoui/Holo-light-ldpi.json"));

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		tokenField = new TextField("", skin);
		urlField = new TextField("", skin);
		TextButton startServerButton = new TextButton("Start server with token", skin);
		TextButton joinServerButton = new TextButton("Join server at URL", skin);
		TextButton backButton = new TextButton("Cancel", skin);
		tokenField.addListener(new ConnectFieldAndButtonListener(startServerButton, tokenField));
		urlField.addListener(new ConnectFieldAndButtonListener(joinServerButton, urlField));
		startServerButton.addListener(new StartServerButtonListener());
		startServerButton.setDisabled(true);
		joinServerButton.addListener(new JoinServerButtonListener());
		joinServerButton.setDisabled(true);
		backButton.addListener(new BackToMainMenuListener());

		root.add(new Label("Enter the developer's one and only ngrok authtoken to start the server, or ask the server owner for the randomly-generated URL.", skin));
		root.row();
		root.add(new Label("Token:", skin));
		root.add(tokenField);
		root.row();
		root.add(new Label("URL:", skin));
		root.add(urlField);
		root.row();
		root.add(startServerButton);
		root.add(joinServerButton);
		root.add(backButton);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

    @Override
    public void dispose() {
        stage.dispose();
		skin.dispose();
    }

	//private classes to avoid nested listeners

	private class StartServerButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			new Thread(new Runnable() {
				public void run() {
					ServerSocketHints hints = new ServerSocketHints();
					hints.acceptTimeout = 300000; //5 minutes
					ServerSocket ss = Gdx.net.newServerSocket(Protocol.TCP, 9021, hints);
					Socket socket = null;
					try {
						socket = ss.accept(null);
					} catch (GdxRuntimeException e) {
						e.printStackTrace();
					}
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println("message from server");
					try {
						System.out.println("the client said:" + in.readLine());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();

			try {
				JavaNgrokConfig config = new JavaNgrokConfig.Builder()
					.withAuthToken(tokenField.getText())
					.build();
				NgrokClient client = new NgrokClient.Builder()
					.withJavaNgrokConfig(config)
					.build();
				CreateTunnel createTunnel = new CreateTunnel.Builder()
					.withProto(Proto.TCP)
					.withAddr(9021)
					.build();
				client.connect(createTunnel);
			} catch (NgrokException ne) {
				root.setTouchable(Touchable.disabled);
				root.setColor(0f, 0f, 0f , 0.2f);
				tokenField.setDisabled(true);
				urlField.setDisabled(true);
				Table popup = new Table();
				popup.setFillParent(true);
				stage.addActor(popup);

				popup.add(new Label("Connection failed\nMake sure you entered the authtoken correctly, and that you're the first one on the server.", skin));
				popup.row();
				TextButton backButton = new TextButton("Try again", skin);
				backButton.addListener(new PopupBackButtonListener(popup));
				popup.add(backButton);
			}
		}
	}

	private class JoinServerButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			new Thread(new Runnable() {
				public void run() {
					SocketHints hints = new SocketHints();
					hints.connectTimeout = 4000; //4 seconds
					Socket socket = null;
					try {
						String[] splitUrl = urlField.getText().split(":");
						socket = Gdx.net.newClientSocket(Protocol.TCP, splitUrl[0], Integer.parseInt(splitUrl[1]), hints);
					} catch (GdxRuntimeException e) {
						e.printStackTrace();
					}
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println("message from client");
					try {
						System.out.println("the server said:" + in.readLine());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	private class ConnectFieldAndButtonListener extends ChangeListener {
		private TextField field;
		private TextButton button;

		public ConnectFieldAndButtonListener(TextButton button, TextField field) {
			this.field = field;
			this.button = button;
		}

		@Override
		public void changed(ChangeEvent event, Actor actor) {
			button.setDisabled(field.getText().length() == 0);
		}
	}

	private class PopupBackButtonListener extends ChangeListener {
		private Table popup;

		public PopupBackButtonListener(Table popup) {
			this.popup = popup;
		}

		@Override
		public void changed(ChangeEvent event, Actor actor) {
			root.setTouchable(Touchable.enabled);
			root.setColor(0f, 0f, 0f, 1f);
			popup.remove();
			tokenField.setDisabled(false);
			urlField.setDisabled(false);
		}
	}

	private class BackToMainMenuListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			game.setScreen(new MainMenuScreen(game));
			dispose();
		}
	}
}
