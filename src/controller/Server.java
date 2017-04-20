package controller;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import model.commands.Command;
import model.commands.Drawing;
import model.commands.ServerInterface;
import model.graphics.BorderBox;
import model.graphics.Drawable;
import model.graphics.FillerBox;
import model.graphics.HollowDot;
import model.graphics.LaserDot;
import model.graphics.ScreenText;
import view.Screen;

/**
 * The server for storing active connection data and responding to connected
 * clients. Does the basic command executions such as drawing objects to the
 * screen and moving a user's icon accross the screen.
 * 
 * @author William Snider
 * 
 */
public class Server implements ServerInterface {
    private Screen screen;
    private Random rnd;

    protected ServerSocket socket = null;
    protected HashMap<String, ObjectInputStream> inputStreams;
    protected HashMap<String, ObjectOutputStream> outputStreams;
    protected HashMap<String, Drawable> pointers;
    protected HashMap<String, LinkedList<Command<Server>>> histories;

    public Server(int port) {
	inputStreams = new HashMap<String, ObjectInputStream>();
	outputStreams = new HashMap<String, ObjectOutputStream>();
	histories = new HashMap<String, LinkedList<Command<Server>>>();
	pointers = new HashMap<String, Drawable>();

	this.screen = new Screen(pointers);
	rnd = new Random();

	try {
	    socket = new ServerSocket(port);

	    System.out.println("Server port: " + port);

	    new ClientAcceptor(this).start();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public synchronized void disconnect(String id) {
	System.out.println(id + " has disconnected!");
	try {
	    inputStreams.remove(id).close();
	    outputStreams.remove(id).close();
	    histories.remove(id);
	    pointers.remove(id);
	    screen.repaint();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void draw(String owner, Drawing draw, int width, int height,
	    String extra) {
	Drawable oldPointer = pointers.get(owner);

	if (oldPointer == null) {
	    return;
	}

	int x = oldPointer.getStartLocation().x;
	int y = oldPointer.getStartLocation().y;
	Color color = oldPointer.getColor();

	Drawable drawing = null;
	switch (draw) {
	case RECTANGLE:
	    drawing = new BorderBox(x, y, width, height, color.getRed(),
		    color.getGreen(), color.getBlue());
	    break;
	case RECTANLE_FILLED:
	    drawing = new FillerBox(x, y, width, height, color.getRed(),
		    color.getGreen(), color.getBlue());
	    break;
	case CIRCLE:
	    drawing = new HollowDot(x, y, width, height, color.getRed(),
		    color.getGreen(), color.getBlue());
	    break;
	case CIRCLE_FILLED:
	    drawing = new LaserDot(x, y, width, height, color.getRed(),
		    color.getGreen(), color.getBlue());
	    break;
	case TEXT:
	    drawing = new ScreenText(extra, x, y, color.getRed(),
		    color.getGreen(), color.getBlue());
	    break;
	default:
	    return;
	}

	drawing.setHidden(true);
	pointers.put(owner, drawing);
	screen.repaint();
    }

    @Override
    public void send(Command<?> cmd) {
	// nothing
    }

    public void send(String id, Object obj) {
	try {
	    outputStreams.get(id).writeObject(obj);
	} catch (IOException e) {
	    System.out.println("Failed to send message to \"" + id + "\"!");
	}
    }

    @Override
    public void move(String id, float x, float y) {
	Drawable drawing = pointers.get(id);
	drawing.addPosition((int) x, (int) y);
	screen.repaint();
    }

    @Override
    public void erase(String id, boolean hide) {
	pointers.get(id).setHidden(hide);
	screen.repaint();
    }

    @Override
    public void changeColor(String id, int red, int green, int blue) {
	pointers.get(id).setColor(red, green, blue);
	screen.repaint();
    }

    public synchronized Color getUniqueColor() {
	Color color = new Color(rnd.nextInt(256), rnd.nextInt(256),
		rnd.nextInt(256));
	Collection<Drawable> pointerList = pointers.values();

	boolean flag = true;
	while (flag) {
	    flag = false;
	    for (Drawable p : pointerList) {
		if (p.isSimilarColor(color, 30.0)) {
		    flag = true;
		    color = new Color(rnd.nextInt(256), rnd.nextInt(256),
			    rnd.nextInt(256));
		    break;
		}
	    }
	}
	return color;
    }
}
