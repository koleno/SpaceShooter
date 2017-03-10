package xyz.koleno.SpaceShooter;

import com.jogamp.opengl.GL2;

/**
 * Represents ship in the game
 * 
 * @author Dusan Koleno
 *
 */
public class Ship implements IScene {
	
	// ship's parameters
	public static float SHIP_DEPTH = 1.5f;
	public static float SHIP_LENGTH = 6.0f;
	public static float SHIP_HEIGHT = 6.0f;
	public static float SHIP_Y = 0.5f; // position on y-axis
	
	private boolean beginner; // beginner's mode flag
	
	// OpenGL access
	private GL2 gl;

	public Ship(GL2 gl) {
		this.gl = gl;
		this.beginner = false;
	}
	
	/**
	 * Renders a ship
	 */
	@Override
	public void render() {
		gl.glDisable(GL2.GL_LIGHTING); // disable rendering of shades for the ship, since it is constantly under the spotlight
		gl.glPushMatrix();
		gl.glTranslatef(0, SHIP_Y, 0);
		gl.glColor3f(1, 0, 0);

		// top vertex, left bottom vertex, center bottom vertex, right bottom vertex
		float[][] shipVertices2D = { { 0.0f, SHIP_HEIGHT/2.0f }, { -SHIP_LENGTH/2.0f, -SHIP_LENGTH/2.0f }, { 0.0f, -(SHIP_HEIGHT/2.0f)*0.3f}, { SHIP_LENGTH/2.0f, -SHIP_LENGTH/2.0f } };
		float[] shipZ = { -SHIP_DEPTH/2.0f, SHIP_DEPTH/2.0f };
		float[][] shipColors = {{0.89f,0.83f,0.19f}, {0.62f,0.47f,0.06f}, {0.62f,0.47f,0.06f}, {0.62f,0.47f,0.06f}, {0.62f,0.47f,0.06f}};
		renderFaces(shipVertices2D, shipZ, shipColors);
		
		
		// bottom right plane, top right plane, bottom left plane, top left plane
		float[][] sideVertices = {{ SHIP_LENGTH/2.0f, -SHIP_LENGTH/2.0f }, { 0.0f, -(SHIP_HEIGHT/2.0f)*0.3f }, { SHIP_LENGTH/2.0f, -SHIP_LENGTH/2.0f }, { 0.0f, SHIP_HEIGHT/2.0f }, { -SHIP_LENGTH/2.0f, -SHIP_LENGTH/2.0f }, { 0.0f, -(SHIP_HEIGHT/2.0f)*0.3f }, { -SHIP_LENGTH/2.0f, -SHIP_LENGTH/2.0f }, { 0.0f, SHIP_HEIGHT/2.0f }};
		renderSides(sideVertices, shipZ);
		
		if(beginner) { // render line going from the ship for beginners
			gl.glBegin(GL2.GL_LINES);
				gl.glColor3f(1.0f, 1.0f, 1.0f);
				gl.glVertex3f(0.0f, SHIP_HEIGHT/2.0f, 0);
				gl.glVertex3f(0.0f, SHIP_HEIGHT/2.0f + Target.TARGET_Y + 2.0f, 0);				
			gl.glEnd();
		}
		 
		gl.glPopMatrix();
		gl.glEnable(GL2.GL_LIGHTING); // enable lights back for other objects
	}
	
	/**
	 * Renders faces of the ship using triangle fan
	 * @param shipVertices2D main vertices
	 * @param shipZ positions on the Z axis, each position = separate ship plane
	 */
	private void renderFaces(float[][] shipVertices2D, float[] shipZ, float[][] shipColors) {
		for (int j = 0; j < shipZ.length; j++) { // depth of the ship => creating two planes back and front
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
						
			for (int i = 0; i < shipVertices2D.length; i++) {
				gl.glColor3f(shipColors[i][0], shipColors[i][1], shipColors[i][2]);
				gl.glVertex3f(shipVertices2D[i][0], shipVertices2D[i][1], shipZ[j]);
			}
			gl.glEnd();
			
		}		
	}
	
	/**
	 * Renders sides of the ship using triangle strip
	 * Every couple of side vertices is combined with z-vertices to create a 4 vertices for a rectangle made from two triangles
	 * 
	 * @param sideVertices main vertices
	 * @param shipZ z-axis for the main vertices
	 */
	private void renderSides(float[][] sideVertices, float[] shipZ) {
		gl.glColor3f(0.41f, 0.48f, 0.8f);
		
		for(int i = 0; i < sideVertices.length; i++) {
			if(i % 2 == 0) { // start new strip for every two vertices
				gl.glBegin(GL2.GL_TRIANGLE_STRIP);
			}
			
			for(int j = shipZ.length - 1; j >= 0; j--) { // combining with z vertices
				gl.glVertex3f(sideVertices[i][0], sideVertices[i][1], shipZ[j]);				
			}
			
			if(i % 2 == 1) {
				gl.glEnd();
			}
		}		
	}
	
	/**
	 * Beginner mode
	 */
	public void toggleBeginner() {
		this.beginner = !this.beginner;
	}

}
