package xyz.koleno.SpaceShooter;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Missile
 * @author Dusan Koleno
 *
 */
public class Missile implements IScene {

	// missile's static parameters
	public static float MISSILE_RADIUS = 0.5f;
	public static float MISSILE_START_Y = Ship.SHIP_Y + Ship.SHIP_HEIGHT/2.0f;
	public static float MISSILE_CUTOFF = MISSILE_START_Y + 6.0f;
	
	// missile's parameters
	float x;
	float y;
	float z;
	private boolean active;
	
	// OpenGL access
	private GL2 gl;
	private GLUT glut;
	
	public Missile(GL2 gl, float x, float z) {
		this.gl = gl;
		this.glut = new GLUT();
		
		this.x = x;
		this.z = z;
		this.y = MISSILE_START_Y;
		
		this.active = true;
	}
	
	/**
	 * Renders a missile
	 */
	@Override
	public void render() {
		if(y >= MISSILE_CUTOFF && active) { // set as inactive if it is too high in the sky
			active = false;
		}
		
		if(active) { // render only if active (two cases if not active => hit a target, too high in the sky)
			gl.glPushMatrix();
	
			gl.glColor3f(1,0,0);
			gl.glTranslatef(x, y, z);
			gl.glRotatef(90,1,0,0);
			glut.glutSolidSphere(MISSILE_RADIUS, Game.RENDER_TESSELLATION, Game.RENDER_TESSELLATION);
			
			gl.glPopMatrix();
			
			y += 0.1f;
		}
	}
	
	// getters and setters
	public float getX() {
		return this.x;
	}
	
	public float getZ() {
		return this.z;
	}
	
	public float getY() {
		return this.y;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

}
