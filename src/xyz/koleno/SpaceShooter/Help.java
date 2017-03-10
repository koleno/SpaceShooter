package xyz.koleno.SpaceShooter;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Renders Help
 * @author Dusan Koleno
 *
 */
public class Help implements IScene {
	
	// canvas size for text placement	
	private int canvasHeight;	
	
	// indicates whether the help is shown or not
	private boolean shown;	
	
	// OpenGL access
	private GL2 gl;
	private GLUT glut;	
	
	public Help(GL2 gl, int canvasHeight) {
		this.gl = gl;
		this.glut = new GLUT();
		
		this.canvasHeight = canvasHeight;
		this.shown = false;
	}
	
	@Override
	/**
	 * Renders the help if shown is true
	 */
	public void render() {
		if(shown) {
			renderHelp();
		}
	}
	
	/**
	 * Renders the help
	 */
	private void renderHelp() {
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		
		// strings to display
		String message[] = new String[7];
		message[0] = "Help:";
		message[1] = "ARROWS - ship movement";
		message[2] = "SHIFT + ARROWS - viewing angle";
		message[3] = "NUMPAD -/+ - zooming";
		message[4] = "NUMPAD 0 - default angle/zoom";
		message[5] = "HOME/ESC - close the window";
		message[6] = "F2 - beginner's mode";
		
		float yPosition = (15 + 10)*(message.length/2.0f);
		for(int i = 0; i < message.length; i++) {
			gl.glWindowPos2f(10, (canvasHeight / 2.0f) + yPosition); 		
			glut.glutBitmapString(GLUT.BITMAP_9_BY_15, message[i]);	
			yPosition = yPosition - 10 - 15; // 15 is size of the one letter, 10 is margin
		}		
	}
	
	/**
	 * Switches the 'shown' variable
	 */
	public void toggleShown() {
		this.shown = !this.shown;
	}
	
	// getter & setters
	public boolean isShown() {
		return this.shown;
	}

	public void setCanvasHeight(int height) {
		this.canvasHeight = height;
	}		

	
}
