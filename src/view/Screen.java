package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import model.graphics.Drawable;

import com.sun.awt.AWTUtilities;

/**
 * The server GUI (which is a full screen completely transparent window that can
 * be clicked through on windows) for our mobile application.
 * 
 * @author William Snider
 * 
 */
@SuppressWarnings("serial")
public class Screen extends JFrame {
    private static final String APPLICATION_NAME = "Lazer Pointer";

    private ContentPanel panel;

    public Screen(HashMap<String, Drawable> drawings) {
	initWindow(drawings);

	this.setVisible(true);
    }

    private void initWindow(HashMap<String, Drawable> drawings) {
	// Initializing window settings
	this.setTitle(APPLICATION_NAME);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.getContentPane().setLayout(null);
	try {
	    this.setIconImage(ImageIO.read(ResourceLoader
		    .load("images/icon.png")));
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// Getting screen dimensions
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	// Setting window to fullscreen, always on top, and borderless for
	// overlay
	this.getContentPane().setPreferredSize(screenSize);
	this.setResizable(false);
	this.setAlwaysOnTop(true);
	this.setUndecorated(true);
	this.pack();

	// Creating content panel for graphical drawing
	panel = new ContentPanel(drawings);

	// Setting content panel to encompass all of overlaying window and be
	// transparent
	panel.setLocation(0, 0);
	panel.setSize(screenSize);
	panel.setOpaque(false);
	this.add(panel);

	// Setting overall window to transparent
	this.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
	AWTUtilities.setWindowOpaque(this, false);
    }
}
