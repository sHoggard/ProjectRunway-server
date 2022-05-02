package server;

import javax.swing.JFrame;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

/**
 * MovableWindow is exactly what it sounds like. It is also a JFrame. 
 * 
 * @author Någon på internet, Sebastian Hoggard
 *
 */
public class MovableWindow extends JFrame implements MouseInputListener {
	private Point dragPoint;
	
	public MovableWindow()
    {
        addMouseMotionListener( this );
        addMouseListener(this);
    }
  
    public void mouseDragged( MouseEvent event )
    {
    	if (dragPoint != null) {
	        Rectangle size = getBounds();
	        setBounds( event.getXOnScreen() - dragPoint.x, event.getYOnScreen() - dragPoint.y, size.width, size.height );
    	}
    }
  
    public void mouseMoved( MouseEvent event )
    {
    }

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent event) {
    	if (event.getButton() == MouseEvent.BUTTON3) {
			dragPoint = event.getLocationOnScreen();
			dragPoint.x -= getBounds().x;
			dragPoint.y -= getBounds().y;
    	}
    	else {
    		dragPoint = null;
    	}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
  
    public static void main( String args[] )
    {
        MovableWindow mw = new MovableWindow();
        mw.setSize( 200, 200 );
        mw.setVisible( true );
    }
}
