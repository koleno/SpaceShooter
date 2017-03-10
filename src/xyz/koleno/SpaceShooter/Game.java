package xyz.koleno.SpaceShooter;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Game class
 *
 * @author Dusan Koleno
 */
public class Game extends Frame implements KeyListener, GLEventListener {

    private static final long serialVersionUID = -4723964573729501656L; // Frame is Serializable, so unique id is needed, otherwise warning

    // game parameters
    public static final int GAME_LENGTH = 120; // seconds
    public static final int GAME_ANGLE_X_START = 20; // limit on x viewing angle
    public static final int GAME_ANGLE_X_END = 80;
    public static final int GAME_ANGLE_Y_START = -20; // starting angle for the y viewing angle
    public static final int GAME_ZOOM_INIT = 0;
    public static final int GAME_ZOOM_START = -10;
    public static final int GAME_ZOOM_END = 10;

    // window parameters
    public static final String WINDOW_TITLE = "SpaceShooter - Dusan Koleno";
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 400;
    public static final int WINDOW_OFFSET = 40;

    // camera parameters
    public static float CAMERA_X = 0.0f;
    public static float CAMERA_Y = 0.0f;
    public static float CAMERA_Z = 32.0f;

    // general rendering parameters
    public static final int RENDER_TESSELLATION = 50;

    // world components
    private Floor floor;
    private Ship ship;
    private Target[] targets;
    private ArrayList<Missile> missiles;
    private Set<Integer> missilesToRemove;
    private boolean shoot;

    private Controls controls; // game controls

    private Score score; // counters

    private Help help;	// help

    private long endTime; // end game time stamp

    private GLCanvas canvas; // OpenGL canvas

    public Game() {
        super(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocation(WINDOW_OFFSET, WINDOW_OFFSET);

        //Close the window when told to by the OS
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true); // show the window
        initJOGL(); // initialize JOGL
    }

    /**
     * Initializes JOGL
     */
    private void initJOGL() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true); // graphics card acceleration

        // canvas
        canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);

        add(canvas, BorderLayout.CENTER);
        canvas.requestFocus(); // requesting focus, so no need to click into the
        // window at the beginning

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    /**
     * Called by the drawable immediately after the OpenGL context is
     * initialized; the GLContext has already been made current when this method
     * is called.
     *
     * @param drawable The display context to render to
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0, 0, 0, 0);
        gl.glMatrixMode(GL2.GL_PROJECTION);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glClearDepth(1.0f); // Set background depth to farthest
        gl.glEnable(GL2.GL_DEPTH_TEST);   // Enable depth testing for z-culling
        gl.glDepthFunc(GL2.GL_LEQUAL);    // Set the type of depth-test
        gl.glShadeModel(GL2.GL_SMOOTH);   // Enable smooth shading
        gl.glEnable(GL2.GL_LIGHTING);  // enable lighting
        gl.glEnable(GL2.GL_LIGHT0); // enable light #0 - spot light for the ship  
        gl.glEnable(GL2.GL_LIGHT1); // enable light #1 - general light
        gl.glEnable(GL2.GL_COLOR_MATERIAL); // change simple color to material
        gl.glEnable(GL2.GL_NORMALIZE); // normalize normal vectors        
        gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE); // how material made from simple color should respond to light       
        gl.glLoadIdentity();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        initWorld(gl);

    }

    /**
     * Initializes components of the world (floor, rings...)
     */
    private void initWorld(GL2 gl) {
        // end time
        endTime = System.currentTimeMillis() + 1000 * GAME_LENGTH;
        score = new Score(gl, endTime, canvas.getWidth(), canvas.getHeight());

        // help
        help = new Help(gl, canvas.getHeight());

        controls = new Controls(this); // game controls

        // targets
        targets = new Target[10];
        createTargets(gl);

        // missiles
        missiles = new ArrayList<>();
        missilesToRemove = new HashSet<>();
        shoot = false;

        floor = new Floor(gl);
        ship = new Ship(gl);
    }

    /**
     * Creates all 10 targets
     *
     * @param gl
     */
    private void createTargets(GL2 gl) {
        Random random = new Random();
        int generatedTargets = 0;

        // limits for targets
        float xLimit = Floor.FLOOR_SIZE - Ship.SHIP_LENGTH;
        float zLimit = Floor.FLOOR_SIZE - Ship.SHIP_DEPTH;

        while (generatedTargets < 10) { // generate 10 targets for the beginning
            float x = (random.nextFloat() * xLimit) - xLimit / 2; // coordinates are created in such a way that no target is too close
            float z = (random.nextFloat() * zLimit) - zLimit / 2; // to the walls, so the ship can reach it with its missiles

            if (targetExists(x, z, 2.0f) == -1) { // generate target only if there is not any in the same/similar position
                targets[generatedTargets] = new Target(gl, x, z);
                generatedTargets++;
            }

        }

        score.increaseTargetsAppeared(10); // update counter		
    }

    /**
     * Creates one target
     *
     * @param gl
     * @param i position for a new target
     */
    private void createSingleTarget(GL2 gl, int i) {
        if (System.currentTimeMillis() < endTime) { // only when game is running
            Random random = new Random();
            boolean generated = false;

            // limits for targets
            float xLimit = Floor.FLOOR_SIZE - Ship.SHIP_LENGTH;
            float zLimit = Floor.FLOOR_SIZE - Ship.SHIP_DEPTH;

            while (!generated) {
                float x = (random.nextFloat() * xLimit) - xLimit / 2; // coordinates are created in such a way that no target is too close
                float z = (random.nextFloat() * zLimit) - zLimit / 2; // to the walls, so the ship can reach it with its missiles

                if (targetExists(x, z, 2.0f) == -1) { // generate target only if there is not any in the same/similar position
                    targets[i] = new Target(gl, x, z);
                    generated = true;
                }
            }

            score.increaseTargetsAppeared();
        }
    }

    /**
     * Determines whether target exists at the specified coordinates
     *
     * @param x
     * @param z
     * @param tolerance adds to the radius so even if x,z are out of actual
     * circle, method will report target as existing if it is within tolerance
     * @return target's index if exists, -1 if not
     */
    private int targetExists(float x, float z, float tolerance) {
        for (int i = 0; i < targets.length; i++) {
            if (targets[i] == null) {
                continue;
            }

            // Checks whether x,z is in the circle, formula from: http://stackoverflow.com/a/481150 (which is just equation of a circle_
            if (Math.pow(x - targets[i].getX(), 2) + Math.pow(z - targets[i].getZ(), 2) < Math.pow(Target.TARGET_OUTER_RADIUS + tolerance, 2)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Displays the scene
     */
    @Override
    public void display(GLAutoDrawable drawable) {

        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); // Clear color and depth buffer	

        gl.glPushMatrix();

        if (shoot) { // shoot a missile
            shoot(gl);
        }

        // viewing angle
        gl.glTranslatef(0.0f, 0.0f, controls.getZoom());
        gl.glRotatef(controls.getAngleY(), 0.0f, 1.0f, 0.0f);
        gl.glRotatef(controls.getAngleX(), 1.0f, 0.0f, 0.0f);

        // light
        light(gl);

        // objects
        renderTargets(gl);
        renderMissiles(gl);
        floor.render();
        renderShip(gl);
        score.render();
        help.render();

        gl.glPopMatrix();

    }

    /**
     * Renders and manages targets in the scene
     *
     * @param gl
     */
    private void renderTargets(GL2 gl) {
        for (int i = 0; i < targets.length; i++) { // render all active targets and create a new ones if inactive
            if (targets[i].isActive()) {
                targets[i].render();
            } else {
                createSingleTarget(gl, i);
            }
        }
    }

    /**
     * Renders and manages missiles in the scene
     *
     * @param gl
     */
    private void renderMissiles(GL2 gl) {
        int i = 0;
        for (Missile m : missiles) { // render all active missiles
            if (!m.isActive()) { // missile is inactive skip this iteration and mark it for removal
                missilesToRemove.add(i);
                continue;
            }

            int index = targetExists(m.getX(), m.getZ(), 1);
            if (index >= 0 && m.getY() >= Target.TARGET_Y) { // missile hit a target, remove target and deactivate missile
                Tools.playSound("boom.wav"); // http://soundbible.com/1793-Flashbang.html (public domain)
                targets[index] = new Target(gl, 1000, 1000);
                m.setActive(false);
                score.increaseTargetsDestroyed();
            }

            m.render();
            i++;
        }

        removeMissiles();
    }

    /**
     * Removes missiles that are inactive from the list
     */
    private void removeMissiles() {
        for (int i : missilesToRemove) {
            missiles.remove(i);
        }

        missilesToRemove.clear();
    }

    /**
     * Renders ship
     *
     * @param gl
     */
    private void renderShip(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(controls.getShipX(), 0, controls.getShipZ()); // move ship to its position
        ship.render();
        gl.glPopMatrix();
    }

    /**
     * Light in the scene
     *
     * @param gl
     */
    public void light(GL2 gl) {
        // light 0 for the ship and its surroundings
        float[] spotDirection = {0.0f, -1.0f, 0.0f}; // direction of spotlight
        float[] lightPosition = {controls.getShipX(), Target.TARGET_Y * 3.5f, controls.getShipZ(), 1}; // position of the light
        float[] ambientLight = {0.5f, 0.5f, 0.5f, 0.1f}; // ambient light parameters

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0); // set position
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_CUTOFF, 45.0f); // angle of the light
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_EXPONENT, 30.0f);  // light attenuation  
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPOT_DIRECTION, spotDirection, 0);  // set direction
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0); // set ambient light

        // light 1 for the whole scene
        float[] diffuseLight = {0.7f, 0.7f, 0.7f, 0.1f}; //diffuse light parameters        
        float[] light1Position = {00f, Target.TARGET_Y, CAMERA_Z, 1.0f}; // position of the light        
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuseLight, 0); // set diffuse
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, light1Position, 0); // set position
    }

    /**
     * Called by the drawable when the surface resizes itself. Used to reset the
     * viewport dimensions.
     *
     * @param drawable The display context to render to
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        score.setCanvasHeight(canvas.getHeight());
        score.setCanvasWidth(canvas.getWidth());
        help.setCanvasHeight(canvas.getHeight());

        GL2 gl = drawable.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);

        // Compute aspect ratio of the new window
        if (height == 0) {
            height = 1; // To prevent divide by 0
        }
        float aspect = (float) width / (float) height;

        // Set the aspect ratio of the clipping volume to match the viewport
        gl.glMatrixMode(GL2.GL_PROJECTION);  // To operate on the Projection matrix
        gl.glLoadIdentity();
        gl.glViewport(0, 0, width, height);

        glu.gluPerspective(45, aspect, 0.1f, 100.0f);
        glu.gluLookAt(CAMERA_X, CAMERA_Y, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /**
     * Called on key press
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // close on HOME or ESC
        if (key == KeyEvent.VK_HOME || key == KeyEvent.VK_ESCAPE) {
            closeWindow();
        }

        if (System.currentTimeMillis() <= endTime) { // allow game controls only if the game is running
            controls.processKey(e);
        }
    }

    /**
     * Shoots a missile from ship's position
     *
     * @param gl
     */
    private void shoot(GL2 gl) {
        shoot = false;
        Missile m = new Missile(gl, controls.getShipX(), controls.getShipZ());
        missiles.add(m);
        Tools.playSound("blaster.wav");
        score.increaseMissilesCounter();
    }

    /**
     * Sets shoot flag
     *
     * @param shoot
     */
    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

    /**
     * Shows help
     */
    public void toggleHelp() {
        this.help.toggleShown();
    }

    /**
     * Toggles beginner's mode
     */
    public void toggleBeginner() {
        this.ship.toggleBeginner();
    }

    /**
     * Close window
     */
    private void closeWindow() {
        System.exit(0);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // unused
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // unused
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
        // unused
    }

    public static void main(String[] args) {
        Game g = new Game(); // start the game
        g.setVisible(true);
    }

}
