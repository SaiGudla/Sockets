package chatBox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String userName;

	public Client(Socket socket, String userName) {
		try {
			this.socket = socket;
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.userName = userName;
		} catch (IOException e) {
			closeEverything(socket, reader, writer);
		}
	}

	public void sendMessage() {
		try {
			writer.write(userName);
			writer.newLine();
			writer.flush();

			Scanner sc = new Scanner(System.in);
			while (socket.isConnected()) {
				String msg = sc.nextLine();
				writer.write(userName + " : " + msg);
				writer.newLine();
				writer.flush();
			}
		} catch (Exception e) {
			closeEverything(socket, reader, writer);

		}
	}

	public void listenForMessage() {
		new Thread(new Runnable() {
			public void run() {
				String msgFromChat;
				while (socket.isConnected()) {
					try {
						msgFromChat = reader.readLine();
						System.out.println(msgFromChat);
					} catch (IOException e) {

					}
				}
			}
		}).start();
	}

	public void closeEverything(Socket s, BufferedReader r, BufferedWriter w) {
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

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the username to join the chat : ");
		String username = sc.nextLine();
		try {
			Socket socket = new Socket("localhost", 1234);
			Client c = new Client(socket, username);
			c.listenForMessage();
			c.sendMessage();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sc.close();
	}
}
