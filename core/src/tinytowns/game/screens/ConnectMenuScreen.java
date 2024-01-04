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
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import tinytowns.game.TinyTowns;

public class ConnectMenuScreen extends AbstractScreen {
	private TinyTowns game;

	private Skin skin;

	private Stage stage;
	private VerticalGroup root;
	private Label compCount;
	private Label humanCount;
	private Label numPlayersLabel;
	private TextButton clientSettingsButton;
	private TextButton compUp;
	private TextButton compDown;
	private TextButton connectButton;
	private TextButton hostSettingsButton;
	private TextButton humanUp;
	private TextButton humanDown;
	private TextButton startGameButton;
	private TextButton startHostingButton;
	private TextField clientNameField;
	private TextField descField;
	private TextField ipField;
	private TextField hostNameField;

	private int connectedPlayers = -1;
	private int totalPlayers = -1;
	private String hostName;
	private String gameDesc;
	private String clientName;
	private String targetIP;
	private static ServerSocket ss;

	public ConnectMenuScreen(TinyTowns game) {
		super(game);

		skin = new Skin(Gdx.files.internal("holoui/Holo-light-ldpi.json"));

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		Table realRoot = new Table(); //centers the verticalgroup
		realRoot.setFillParent(true);
		stage.addActor(realRoot);
		root = new VerticalGroup();
		realRoot.add(root);

		hostSettingsButton = new TextButton("Host a game", skin);
		hostSettingsButton.addListener(new HostSettingsButtonListener());
		root.addActor(hostSettingsButton);

		clientSettingsButton = new TextButton("Connect to a host", skin);
		clientSettingsButton.addListener(new ClientSettingsButtonListener());
		root.addActor(clientSettingsButton);

		TextButton backButton = new TextButton("Back", skin);
		backButton.addListener(new ScreenBackButtonListener());
		root.addActor(backButton);

		root.pack();
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
		if (ss != null)
			ss.dispose();
    }

	private void updatePlayerCountButtons(int humanCount, int compCount) {
		humanUp.setDisabled(false);
		humanDown.setDisabled(false);
		compUp.setDisabled(false);
		compDown.setDisabled(false);

		if (humanCount + compCount >= 6) {
			humanUp.setDisabled(true);
			compUp.setDisabled(true);
		}
		if (humanCount + compCount <= 2) {
			humanDown.setDisabled(true);
			compDown.setDisabled(true);
		}
		if (humanCount <= 2)
			humanDown.setDisabled(true);
		if (compCount <= 0)
			compDown.setDisabled(true);
		if (compCount >= 4)
			compUp.setDisabled(true);
	}

	//private classes to avoid nested listeners

	private class HostSettingsButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			root.setTouchable(Touchable.disabled);
			root.setColor(0f, 0f, 0f , 0.2f);
			Table popup = new Table();
			popup.setFillParent(true);
			stage.addActor(popup);

			humanCount = new Label("2", skin);
			compCount = new Label("0", skin);
			humanUp = new TextButton("^", skin);
			humanDown = new TextButton("v", skin);
			humanDown.setDisabled(true);
			compUp = new TextButton("^", skin);
			compDown = new TextButton("v", skin);
			compDown.setDisabled(true);

			humanUp.addListener(new HumanUpListener());
			humanDown.addListener(new HumanDownListener());
			compUp.addListener(new CompUpListener());
			compDown.addListener(new CompDownListener());

			popup.add(new Label("Human players:", skin));
			popup.add(humanCount);
			popup.add(humanUp);
			popup.add(humanDown);

			popup.row();
			popup.add(new Label("Computer players:", skin));
			popup.add(compCount);
			popup.add(compUp);
			popup.add(compDown);

			popup.row();
			popup.add(new Label("Your name:", skin));
			hostNameField = new TextField("", skin);
			hostNameField.addListener(new HostFieldsListener());
			popup.add(hostNameField);

			popup.row();
			popup.add(new Label("Game description:", skin));
			descField = new TextField("", skin);
			descField.addListener(new HostFieldsListener()); 
			popup.add(descField);

			popup.row();
			startHostingButton = new TextButton("Create game", skin);
			startHostingButton.setDisabled(true);
			startHostingButton.addListener(new StartHostingButtonListener());
			startHostingButton.addListener(new PopupBackButtonListener(popup));
			popup.add(startHostingButton);

			popup.row();
			TextButton backButton = new TextButton("Back", skin);
			backButton.addListener(new PopupBackButtonListener(popup));
			popup.add(backButton);
		}
	}

	private class ClientSettingsButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			root.setTouchable(Touchable.disabled);
			root.setColor(0f, 0f, 0f , 0.2f);

			Table popup = new Table();
			popup.setFillParent(true);
			stage.addActor(popup);

			popup.add(new Label("Your name:", skin));
			clientNameField = new TextField("", skin);
			clientNameField.addListener(new ClientFieldsListener());
			popup.add(clientNameField);

			popup.row();
			popup.add(new Label("Host's IP address:", skin));
			ipField = new TextField("", skin);
			ipField.addListener(new ClientFieldsListener());
			popup.add(ipField);

			popup.row();
			connectButton = new TextButton("Connect", skin);
			connectButton.setDisabled(true);
			connectButton.addListener(new ConnectButtonListener());
			connectButton.addListener(new PopupBackButtonListener(popup));
			popup.add(connectButton);

			popup.row();
			TextButton backButton = new TextButton("Back", skin);
			backButton.addListener(new PopupBackButtonListener(popup));
			popup.add(backButton);
		}
	}

	private class StartHostingButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			hostName = hostNameField.getText();
			gameDesc = descField.getText();
			connectedPlayers = 1;
			totalPlayers = Integer.valueOf(humanCount.getText().toString());
			new ListenForClientsThread().start();

			clientSettingsButton.setDisabled(true);

			HorizontalGroup hGroup = new HorizontalGroup();
			numPlayersLabel = new Label(gameDesc + "\n" + connectedPlayers + "/" + totalPlayers + " players connected", skin);
			hGroup.addActor(numPlayersLabel);

			startGameButton = new TextButton("Start game", skin);
			startGameButton.addListener(new StartGameButtonListener());
			hGroup.addActor(startGameButton);

			TextButton stopHostingButton = new TextButton("Stop hosting", skin);
			stopHostingButton.addListener(new StopHostingButtonListener());
			hGroup.addActor(stopHostingButton);

			root.addActorAt(0, hGroup);
			root.pack();

			hostSettingsButton.setDisabled(true);
		}
	}
	
	private class StartGameButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			
		}
	}

	private class StopHostingButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			
		}
	}

	private class ConnectButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			clientName = clientNameField.getText();
			targetIP = ipField.getText();
			new ClientToHostThread().start();

			hostSettingsButton.setDisabled(true);

			HorizontalGroup hGroup = new HorizontalGroup();
			hGroup.addActor(new Label("Waiting for host...", skin));
			TextButton cancelConnectionButton = new TextButton("Cancel", skin);
			cancelConnectionButton.addListener(new CancelConnectionButtonListener());
			hGroup.addActor(cancelConnectionButton);

			root.addActorAt(0, hGroup);
			root.pack();
		}
	}

	private class CancelConnectionButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			
		}
	}

	private class HumanUpListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			int hc = Integer.valueOf(humanCount.getText().toString());
			humanCount.setText(hc + 1);
			hc++;
			int cc = Integer.valueOf(compCount.getText().toString());
			updatePlayerCountButtons(hc, cc);
		}
	}

	private class HumanDownListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			int hc = Integer.valueOf(humanCount.getText().toString());
			humanCount.setText(hc - 1);
			hc--;
			int cc = Integer.valueOf(compCount.getText().toString());
			updatePlayerCountButtons(hc, cc);
		}
	}

	private class CompUpListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			int hc = Integer.valueOf(humanCount.getText().toString());
			int cc = Integer.valueOf(compCount.getText().toString());
			compCount.setText(cc + 1);
			cc++;
			updatePlayerCountButtons(hc, cc);
		}
	}
	
	private class CompDownListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			int hc = Integer.valueOf(humanCount.getText().toString());
			int cc = Integer.valueOf(compCount.getText().toString());
			compCount.setText(cc - 1);
			cc--;
			updatePlayerCountButtons(hc, cc);
		}
	}

	private class HostFieldsListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			startHostingButton.setDisabled(hostNameField.getText().length() == 0 || descField.getText().length() == 0);
		}
	}

	private class ClientFieldsListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			connectButton.setDisabled(clientNameField.getText().length() == 0 || ipField.getText().length() == 0);
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
		}
	}

	private class ScreenBackButtonListener extends ChangeListener {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			game.setScreen(new MainMenuScreen(game));
			dispose();
		}
	}

	//threads can't be anonymous because they use non-final variables

	private class ListenForClientsThread extends Thread {
		@Override
		public void run() {
			while (connectedPlayers != -1 && totalPlayers != -1) {
				ServerSocketHints ssh = new ServerSocketHints();
				ssh.acceptTimeout = 300000; //5 minutes
				ss = Gdx.net.newServerSocket(Protocol.TCP, 9021, ssh);
				Socket socket;
				try {
					socket = ss.accept(null);
				} catch (GdxRuntimeException e) {
					root.removeActorAt(0, false);
					root.setTouchable(Touchable.disabled);
					root.setColor(0f, 0f, 0f , 0.2f);
					root.pack();

					Table popup = new Table();
					popup.setFillParent(true);
					stage.addActor(popup);
					popup.add(new Label("Connection failed", skin));
					popup.row();
					TextButton confirmButton = new TextButton("OK", skin);
					confirmButton.addListener(new PopupBackButtonListener(popup));
					popup.add(confirmButton);

					break;
				}
				new ReadClientThread(socket).start();
			}
		}
	}

	private class ReadClientThread extends Thread {
		Socket socket;

		public ReadClientThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			try {
				root.setTouchable(Touchable.disabled);
				root.setColor(0f, 0f, 0f , 0.2f);
				Table popup = new Table();
				popup.setFillParent(true);
				stage.addActor(popup);

				popup.add(new Label(in.readLine() + " is trying to connect.\nDo you want them to join your game?", skin));
				popup.row();
				TextButton acceptButton = new TextButton("Yes", skin);
				TextButton denyButton = new TextButton("No", skin);
				acceptButton.addListener(new PopupBackButtonListener(popup));
				denyButton.addListener(new PopupBackButtonListener(popup));
				popup.add(acceptButton);
				popup.add(denyButton);

				out.println(hostName);
				out.println(gameDesc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ClientToHostThread extends Thread {
		@Override
		public void run() {
			SocketHints sh = new SocketHints();
			sh.connectTimeout = 4000; //4 seconds
			Socket socket;
			try {
				socket = Gdx.net.newClientSocket(Protocol.TCP, targetIP, 9021, sh);
			} catch (GdxRuntimeException e) {
				root.removeActorAt(0, false);
				root.setTouchable(Touchable.disabled);
				root.setColor(0f, 0f, 0f , 0.2f);
				root.pack();

				Table popup = new Table();
				popup.setFillParent(true);
				stage.addActor(popup);
				popup.add(new Label("Connection failed", skin));
				popup.row();
				TextButton confirmButton = new TextButton("OK", skin);
				confirmButton.addListener(new PopupBackButtonListener(popup));
				popup.add(confirmButton);

				return;
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			try {
				String recievedHostName = in.readLine();
				String recievedGameDesc = in.readLine();
				Table popup = new Table();
				popup.add(new Label("You have been accepted to " + recievedGameDesc + ", hosted by " + recievedHostName + ".", skin));
				popup.row();
				TextButton confirmButton = new TextButton("OK", skin);
				confirmButton.addListener(new PopupBackButtonListener(popup));
				popup.add();

				out.println(clientName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
