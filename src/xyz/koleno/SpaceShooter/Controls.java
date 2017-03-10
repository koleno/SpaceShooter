package xyz.koleno.SpaceShooter;

import java.awt.event.KeyEvent;

/**
 * In-game controls (movement of ship, zooming, etc.)
 * @author Dusan Koleno
 *
 */
public class Controls {
	
	private Game game;
	
	// viewing angles and zoom
	private int angleY;
	private int angleX;
	private int zoom;
	
	// ship position
	private float shipX;
	private float shipZ;	
	
	public Controls(Game game) {
		this.game = game;
		
		shipX = shipZ = 0.0f; // ship at 0,0,0		
		angleY = 0;
		zoom = Game.GAME_ZOOM_INIT;
		angleX = Game.GAME_ANGLE_X_START;		
		angleY = Game.GAME_ANGLE_Y_START;
	}
	
	protected void processKey(KeyEvent e) {
		int key = e.getKeyCode();
		
		// help
		if(key == KeyEvent.VK_F1) {
			game.toggleHelp();
		}
		
		// beginner's mode
		if(key == KeyEvent.VK_F2) {
			game.toggleBeginner();
		}		
		
		// shoot a missile
		if(key == KeyEvent.VK_SPACE) {
			game.setShoot(true);
		}	
		
		// ship movements (arrows)
		if(key == KeyEvent.VK_UP && !e.isShiftDown()) {
			moveUp();
		}

		if(key == KeyEvent.VK_DOWN && !e.isShiftDown()) {
			moveDown();
		}
		
		if(key == KeyEvent.VK_LEFT && !e.isShiftDown()) {
			moveLeft();
		}
		
		if(key == KeyEvent.VK_RIGHT && !e.isShiftDown()) {
			moveRight();
		}
		
		// point of view changes (arrows + shift)
		if(key == KeyEvent.VK_UP && e.isShiftDown()) {
			moveAngleUp();
		}

		if(key == KeyEvent.VK_DOWN && e.isShiftDown()) {
			moveAngleDown();
		}
		
		if(key == KeyEvent.VK_LEFT && e.isShiftDown()) {
			moveAngleLeft();
		}
		
		if(key == KeyEvent.VK_RIGHT && e.isShiftDown()) {
			moveAngleRight();
		}		
		
		// zoom in and out with camera
		if(key == KeyEvent.VK_ADD) {
			zoomIn();
		}
		
		if(key == KeyEvent.VK_SUBTRACT) {
			zoomOut();
		}
		
		// reset point of view to default if pressed 0
		if(key == KeyEvent.VK_NUMPAD0 || key == KeyEvent.VK_0) {
			angleY = 0;
			zoom = Game.GAME_ZOOM_INIT;
			angleX = Game.GAME_ANGLE_X_START;
		}				
	}
	
	// zooming
	private void zoomIn() {
		if(zoom < Game.GAME_ZOOM_END) {
			zoom++;
		}
	}
	
	private void zoomOut() {
		if(zoom > Game.GAME_ZOOM_START) {
			zoom--;
		}
	}
	
	// ship movement (each movement is limited by floor size including ship's dimensions
	private void moveUp() {
		if(shipZ > -(Floor.FLOOR_SIZE / 2 - Ship.SHIP_DEPTH/1.5f)) {
			shipZ--;
		}
	}
	
	private void moveDown() {
		if(shipZ < Floor.FLOOR_SIZE / 2 - Ship.SHIP_DEPTH/1.5f) {
			shipZ++;
		}	
	}
	
	private void moveLeft() {
		if(shipX >= -(Floor.FLOOR_SIZE/2.0f - Ship.SHIP_LENGTH/1.5f)) {		
			shipX--;
		}
	}
	
	private void moveRight() {
		if(shipX < Floor.FLOOR_SIZE/2.0f - Ship.SHIP_LENGTH/1.5f) {		
			shipX++;
		}
	}
	
	// viewing angle changes
	private void moveAngleLeft() {
		angleY = (angleY - 1) % 360;
	}
	
	private void moveAngleRight() {
		angleY = (angleY + 1) % 360;
	}
	
	private void moveAngleUp() {
		if(angleX < Game.GAME_ANGLE_X_END) {
			angleX = (angleX + 1) % 360;
		}
	}
	
	private void moveAngleDown() {
		if(angleX > Game.GAME_ANGLE_X_START) {
			angleX = (angleX - 1) % 360;		
		}
	}

	// getters and setters
	public int getAngleY() {
		return angleY;
	}

	public int getAngleX() {
		return angleX;
	}

	public int getZoom() {
		return zoom;
	}

	public float getShipX() {
		return shipX;
	}

	public float getShipZ() {
		return shipZ;
	}		
	
	
	
}
