package tinytowns.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.alexdlaird.exception.NgrokException;
import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import tinytowns.game.TinyTowns;

public class ConnectMenuScreen extends MenuScreen {
	private TextField tokenField;
	private TextField urlField;

	public ConnectMenuScreen(TinyTowns game) {
		super(game);

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

		Label instructions = new Label("Enter the developer's one and only ngrok authtoken to start the server, or ask the server owner for the randomly generated URL.", skin);
		instructions.setAlignment(Align.center);
		instructions.setWrap(true);
		root.add(instructions).width(350).colspan(2);
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

	private Table startPopup() {
		root.setTouchable(Touchable.disabled);
		root.setColor(0f, 0f, 0f , 0.2f);
		tokenField.setDisabled(true);
		urlField.setDisabled(true);
		Table popup = new Table();
		popup.setFillParent(true);
		stage.addActor(popup);
		return popup;
	}

	//private classes to avoid nested listeners

	private class StartServerButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
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
				Tunnel tunnel = client.connect(createTunnel);
				game.setNgrokClient(client);
				game.setScreen(new LobbyHostScreen(game, tunnel.getPublicUrl()));
				dispose();
			} catch (NgrokException ne) {
				Table popup = startPopup();
				Label message = new Label("Connection failed\nMake sure you entered the authtoken correctly, and that you're the first one on the server.", skin);
				message.setAlignment(Align.center);
				message.setWrap(true);
				popup.add(message).width(400);
				popup.row();
				TextButton backButton = new TextButton("OK", skin);
				backButton.addListener(new PopupBackButtonListener(popup));
				popup.add(backButton);
			}
		}
	}

	private class JoinServerButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			String[] splitUrl = urlField.getText().split(":");
			if (splitUrl.length != 2) {
				Table popup = startPopup();
				popup.add(new Label("Your URL was formatted incorrectly.", skin));
				popup.row();
				TextButton backButton = new TextButton("OK", skin);
				backButton.addListener(new PopupBackButtonListener(popup));
				popup.add(backButton);
				return;
			}

			Thread connectToServer = new Thread() {
				@Override
				public void run() {
					try {
						SocketHints hints = new SocketHints();
						hints.connectTimeout = 4000; //4 seconds
						Socket socket = Gdx.net.newClientSocket(Protocol.TCP, splitUrl[0], Integer.parseInt(splitUrl[1]), hints);
						//game.setScreen(new LobbyClientScreen(game, socket));
						//dispose();
					} catch (GdxRuntimeException gre) {
						Table popup = startPopup();
						Label message = new Label("Connection failed\nMake sure your URL is correct, and that someone else started the server.", skin);
						message.setWrap(true);
						message.setAlignment(Align.center);
						popup.add(message).width(400);
						popup.row();
						TextButton backButton = new TextButton("OK", skin);
						backButton.addListener(new PopupBackButtonListener(popup));
						popup.add(backButton);
					}
				}
			};
			connectToServer.setDaemon(true);
			connectToServer.start();
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
