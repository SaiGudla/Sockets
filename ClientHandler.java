package chatBox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

	public static ArrayList<ClientHandler> clients = new ArrayList<>();
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String clientName;

	public ClientHandler(Socket socket) {
		try {
			this.socket = socket;
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.clientName = reader.readLine();
			clients.add(this);
			broadCastMessage("SERVER: " + clientName + " has entered the Chat");

		} catch (Exception e) {
			closeEverything(socket, reader, writer);
		}

	}

	@Override
	public void run() {
		String message;

		while (socket.isConnected()) {
			try {
				message = reader.readLine();
				broadCastMessage(message);
			} catch (IOException e) {
				closeEverything(socket, reader, writer);
				break;
			}
		}
	}

	public void broadCastMessage(String message) {
		for (ClientHandler ch : clients) {
			try {
				if (!ch.clientName.equals(clientName)) {
					ch.writer.write(message);
					ch.writer.newLine();
					ch.writer.flush();
				}
			} catch (IOException e) {
				closeEverything(socket, reader, writer);
			}
		}
	}

	public void removeClientHandler() {
		clients.remove(this);
		broadCastMessage("SERVER: " + clientName + " has Left the chat");

	}

	public void closeEverything(Socket s, BufferedReader r, BufferedWriter w) {
		removeClientHandler();
		try {
			if (r != null)
				r.close();
			if (w != null)
				w.close();
			if (s != null)
				s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
