package view;

import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.JPanel;

import model.graphics.Drawable;

/**
 * 
 * The Panel class where all of the actual drawing is occuring, such as drawable
 * objects being moved on the screen.
 * 
 * @author William Snider
 * 
 */
@SuppressWarnings("serial")
public class ContentPanel extends JPanel {
    HashMap<String, Drawable> drawings;

    public ContentPanel(HashMap<String, Drawable> drawings) {
	this.drawings = drawings;
    }

    @Override
    public void paintComponent(Graphics graphics) {
	super.paintComponent(graphics);

	for (Drawable drawing : drawings.values()) {
	    drawing.draw(graphics);
	}
    }
}
