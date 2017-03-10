package xyz.koleno.SpaceShooter;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Target
 * @author Dusan Koleno
 *
 */
public class Target implements IScene {

	// target's static parameters
	public static float TARGET_INNER_RADIUS = 0.3f;
	public static float TARGET_OUTER_RADIUS = 1.0f;
	public static float TARGET_Y = 6.0f;
	
	// target's parameters
	private float x;
	private float z;
	private long disappearTime;
	
	// OpenGL access
	private GL2 gl;
	private GLUT glut;
	
	public Target(GL2 gl, float x, float z) {
		this.gl = gl;
		this.glut = new GLUT();
		
		this.x = x;
		this.z = z;
		this.disappearTime = System.currentTimeMillis() + ((int)(5 + Math.random()*10))*1000; // randomly assign fixed time from 5 to 15 seconds
	}
	
	/**
	 * Renders a target
	 */
	@Override
	public void render() {
		if(disappearTime >= System.currentTimeMillis()) { // render only if target is alive
			gl.glPushMatrix();
	
			gl.glColor3f(0.7f,0.7f,0.0f);
			gl.glTranslatef(x, TARGET_Y, z);
			gl.glRotatef(90,1,0,0);
			// target is a "donut"
			glut.glutSolidTorus(TARGET_INNER_RADIUS, TARGET_OUTER_RADIUS, Game.RENDER_TESSELLATION, Game.RENDER_TESSELLATION);
			
			gl.glPopMatrix();
		}
	}
	
	// getters
	public float getX() {
		return this.x;
	}
	
	public float getZ() {
		return this.z;
	}
	
	public boolean isActive() {
		if(disappearTime >= System.currentTimeMillis()) {
			return true;
		}
		
		return false;
	}

}
