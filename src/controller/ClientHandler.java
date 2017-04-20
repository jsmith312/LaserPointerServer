package controller;

import java.awt.Color;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import model.commands.Command;
import model.commands.DisconnectFromServer;
import model.commands.UndoableCommand;
import model.extra.ColorPoint;

/**
 * Thread is spawned by the client acceptor class and is used to listen to
 * commands being sent from the client mobile app.
 * 
 * @author William Snider
 * 
 */
public class ClientHandler extends Thread {
    private Server server;
    private String id;
    private LinkedList<Command<Server>> history;
    private ObjectInputStream input;

    public ClientHandler(Server server, String id, ObjectInputStream input,
	    LinkedList<Command<Server>> history) {
	this.server = server;
	this.id = id;
	this.history = history;
	this.input = input;
    }

    public void run() {
	Color color = server.getUniqueColor();
	server.changeColor(id, color.getRed(), color.getGreen(),
		color.getBlue());

	server.send(
		id,
		new ColorPoint(color.getRed(), color.getGreen(), color
			.getBlue()));

	while (true) {
	    try {
		System.out.println("Getting object!");
		Object object = input.readObject();
		System.out.println("Received object!");

		if (object instanceof Command<?>) {
		    @SuppressWarnings("unchecked")
		    Command<Server> command = (Command<Server>) object;
		    command.execute(server);

		    if (command instanceof UndoableCommand) {
			history.add(command);
		    } else if (command instanceof DisconnectFromServer) {
			System.out.println("d/c");
			break;
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
		break;
	    }
	}
    }

    public String getOwner() {
	return id;
    }
}
