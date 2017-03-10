package xyz.koleno.SpaceShooter;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Target
 * @author Dusan Koleno
 *
 */
public class Score implements IScene {

	private long endTime; // same as in the Game
	
	// canvas size for text placement
	private int canvasWidth;
	private int canvasHeight;

	// counters
	private int targetsAppeared;
	private int targetsDestroyed;
	private int missilesCounter;	
	
	// OpenGL access
	private GL2 gl;
	private GLUT glut;
	
	public Score(GL2 gl, long endTime, int canvasWidth, int canvasHeight) {
		this.gl = gl;
		this.glut = new GLUT();
		
		this.endTime = endTime;
		this.canvasHeight = canvasHeight;
		this.canvasWidth = canvasWidth;
		
		targetsAppeared = targetsDestroyed = missilesCounter = 0;
	}
	
	/**
	 * Renders score
	 */
	@Override
	public void render() {
		if(System.currentTimeMillis() <= endTime) {
			renderScore();
		} else {
			renderFinalScore();
		}
	}
	
	/**
	 * Renders intermediate score
	 */
	private void renderScore() {		
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		
		// string to print
		String help = "F1 - Help";
		String time = "Time: " + (endTime - System.currentTimeMillis())/1000;
		String appeared = "Targets Appeared: " + targetsAppeared;
		String destroyed = "Targets Destroyed: " + targetsDestroyed;
		String missiles = "Missiles Fired: " + missilesCounter;
		
		GLUT glut = new GLUT();
		// using 10 as margin and, 9 for the letter size
		gl.glWindowPos2f(10, canvasHeight - 9 - 10); // using window pos, so it is positioned according to the window size
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, help);			
		
		gl.glWindowPos2f(canvasWidth/2.0f - 9*time.length()/2, canvasHeight - 9 - 10);
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, time);			
		
		gl.glWindowPos2f(10, 10); 		
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, appeared);	
		
		gl.glWindowPos2f(canvasWidth/2.0f - 9*destroyed.length()/2, 10);		
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, destroyed);
		
		gl.glWindowPos2f(canvasWidth - 9*missiles.length() - 10, 10);		
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, missiles);			
	}
	
	/**
	 * Renders final score
	 */
	private void renderFinalScore() {
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		
		// strings to display
		String over = "Game Over!";
		String appeared = "Targets Appeared: " + targetsAppeared;
		String destroyed = "Targets Destroyed: " + targetsDestroyed;
		String missiles = "Missiles Fired: " + missilesCounter;
		
		 // using window pos, so it is positioned according to the window size		
		gl.glWindowPos2f(canvasWidth/2.0f - 9*over.length()/2, (canvasHeight / 2.0f) + 15 + 10 + 15 + 10); 		
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, over);			
		
		gl.glWindowPos2f(canvasWidth/2.0f - 9*appeared.length()/2, (canvasHeight / 2.0f) + 15 + 10); 		
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, appeared);	
		
		gl.glWindowPos2f(canvasWidth/2.0f - 9*destroyed.length()/2, canvasHeight / 2.0f);		
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, destroyed);
		
		gl.glWindowPos2f(canvasWidth/2.0f - 9*missiles.length()/2, (canvasHeight / 2.0f) - 15 - 10);		
		glut.glutBitmapString(GLUT.BITMAP_9_BY_15, missiles);			
	}
	
	// methods for increasing counters
	public void increaseTargetsAppeared(int n) {
		this.targetsAppeared += n;
	}
	
	public void increaseTargetsAppeared() {
		this.targetsAppeared++;
	}
	
	public void increaseTargetsDestroyed() {
		this.targetsDestroyed++;
	}

	public void increaseMissilesCounter() {
		this.missilesCounter++;
	}
	
	// getters & setters
	public int getTargetsAppeared() {
		return targetsAppeared;
	}

	public int getTargetsDestroyed() {
		return targetsDestroyed;
	}

	public int getMissilesCounter() {
		return missilesCounter;
	}

	public void setCanvasWidth(int width) {
		this.canvasWidth = width;
	}

	public void setCanvasHeight(int height) {
		this.canvasHeight = height;
	}	

	
}
