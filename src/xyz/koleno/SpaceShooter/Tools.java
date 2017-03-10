package xyz.koleno.SpaceShooter;

import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * Tools used in the game
 * @author Dusan Koleno
 *
 */
public abstract class Tools {

	/**
	 * Converts degrees to radians
	 * @param deg input
	 * @return radians
	 */
	public static double deg2rad(int deg) {
		return (deg * Math.PI) / 180;
	}
	
	
	/**
	 * Plays sound in a new thread 
	 * @param url resource to play
	 */
	public static synchronized void playSound(final String url) {
		new Thread(new Runnable() {
			public void run() {
				Timer timer = new Timer(); // used to stop the clip
				
				try {
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(
							getClass().getResourceAsStream("resources/" + url));
					DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat()); // added DataLine.Info to play it correctly on all OSes (I had an issue in Debian)
					final Clip clip = (Clip)AudioSystem.getLine(info);			
					clip.open(inputStream);
					clip.start();
					
					// close the clip after it is finished in order to release resources
					// without it audio was stopping because line was too busy
					timer.schedule(new TimerTask() {
						
						@Override
						public void run() {
							clip.close();
						}
					}, (long)(inputStream.getFrameLength() / inputStream.getFormat().getFrameRate() * 1000)); // length of the sound in milliseconds
				} catch (Exception e) {
					System.err.println(e + ": " + e.getMessage());
				}
			}
		}).start();
	}	
	
	/**
	 * Vector cross product (http://tutorial.math.lamar.edu/Classes/CalcII/CrossProduct.aspx)
	 * @param a 3d vector
	 * @param b 3d vector
	 * @return vector cross product
	 */
	public static float[] crossProduct(float[] a, float[] b) {
		float[] result = {a[1]*b[2] - a[2]*b[1], a[2]*b[0] - a[0]*b[2], a[0]*b[1] - a[1]*b[0]};
		return result;
	}
	
	/**
	 * Vector subtraction
	 * @param a 3d vector
	 * @param b 3d vector
	 * @return vector
	 */
	public static float[] vectorSubtraction(float[] a, float[] b) {
		float[] result = {a[0] - b[0], a[1] - b[1], a[2] - b[2]};
		return result;
	}
}
