package controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import model.commands.Command;
import model.graphics.Drawable;
import model.graphics.ScreenText;

/**
 * Client acceptor solely listens to server socket and accepts all incomming
 * client connections to the server, then passes them off to another thread.
 * 
 * @author William Snider
 * 
 */
public class ClientAcceptor extends Thread {
    private static int count = 0;

    private Server server;

    public ClientAcceptor(Server server) {
	this.server = server;
    }

    public void run() {
	while (true) {
	    try {
		Socket client = server.socket.accept();

		ObjectOutputStream output = new ObjectOutputStream(
			client.getOutputStream());
		ObjectInputStream input = new ObjectInputStream(
			client.getInputStream());

		LinkedList<Command<Server>> history = new LinkedList<Command<Server>>();

		String id = "Client" + count;
		count++;

		output.writeObject(id);

		server.outputStreams.put(id, output);
		server.inputStreams.put(id, input);

		server.histories.put(id, history);
		Drawable drawing = new ScreenText(id, 500, 300, 0, 0, 0);
		drawing.setHidden(true);
		server.pointers.put(id, drawing);

		System.out.println("Client connected!");

		// start a new ClientHandler for this new client
		new ClientHandler(server, id, input, history).start();
	    } catch (Exception e) {
		e.printStackTrace();
		break;
	    }
	}
    }
}
