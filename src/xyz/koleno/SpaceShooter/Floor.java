package xyz.koleno.SpaceShooter;

import java.io.IOException;
import java.io.InputStream;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Floor in the game
 * @author Dusan Koleno
 *
 */
public class Floor implements IScene {

	// floor's static parameters
	public static final float FLOOR_SIZE = 25.0f; // one side of the rectangle
	public static final float[] FLOOR_COLOR = {0.6f, 0.6f, 0.6f};
	public static final float FLOOR_Y = -4.0f; // floor Y positions
	
	private Texture floorTexture;
	
	// vertices, texture coordinates and normal vectors
	float[][] vertices; 
	float[][] textCoords;
	float[][] normalVectors;
	
	// OpenGL access
	private GL2 gl;
	
	public Floor(GL2 gl) {
		this.gl = gl;
		loadTextures();
		generateCoords();
	}
	
    /**
     * Loads textures
     */
    private void loadTextures() {
        try {
            InputStream stream = getClass().getResourceAsStream("resources/floor.jpg"); // http://webtreatsetc.deviantart.com/art/Tileable-Starfield-Patterns-134349776?q=boost%3Apopular%20in%3Aresources%20seamless&qo=211
            floorTexture = TextureIO.newTexture(stream, false, "jpg");
        } catch (IOException e) {
            System.out.println("Floor textures failed.");
        }        
    }		
    
    /**
     * Generates tessellated coordinates
     * It could also be done in the render method, but this is more effective.
     */
    private void generateCoords() {
		// floor is one big rectangle made of 2 triangles		
		float coord = (FLOOR_SIZE / 2.0f); // initial coordinate for the floor
		int rectangles = Game.RENDER_TESSELLATION; // number of rows and columns of rectangles in the floor, thus total number of rectangles is rectangles^2
		float increment = (FLOOR_SIZE / rectangles); // increment use in tessellation
		
		vertices = new float[rectangles*rectangles*4][3];
		textCoords = new float[rectangles*rectangles*4][2];		
    	normalVectors = new float[rectangles*rectangles*4][3];
		
    	int counter = 0;
		for (int v = 0; v < rectangles; v++) {
			for (int h = 0; h < rectangles; h++) {			
				
				// order of vertices: far left, near left, far right, near right
				vertices[counter][0] = coord - h*increment; vertices[counter][1] = FLOOR_Y; vertices[counter][2] = coord - (v+1)*increment;
				vertices[counter+1][0] = coord - h*increment; vertices[counter+1][1] = FLOOR_Y; vertices[counter+1][2] = coord - v*increment;
				vertices[counter+2][0] = coord - (h+1)*increment; vertices[counter+2][1] = FLOOR_Y; vertices[counter+2][2] = coord - (v+1)*increment;
				vertices[counter+3][0] = coord - (h+1)*increment; vertices[counter+3][1] = FLOOR_Y; vertices[counter+3][2] = coord - v*increment; 
			
				// texture coordinates for vertices above
				textCoords[counter][0] = h*(1.0f/rectangles); textCoords[counter][1] = (v+1)*(1.0f/rectangles);
				textCoords[counter+1][0] = h*(1.0f/rectangles); textCoords[counter+1][1] = v*(1.0f/rectangles);
				textCoords[counter+2][0] = (h+1)*(1.0f/rectangles); textCoords[counter+2][1] = (v+1)*(1.0f/rectangles);
				textCoords[counter+3][0] = (h+1)*(1.0f/rectangles); textCoords[counter+3][1] = (v)*(1.0f/rectangles);
				
				// normal vectors for each vertex (http://stackoverflow.com/questions/9806630/calculating-the-vertex-normals-of-a-quad)
				normalVectors[counter] = Tools.crossProduct(Tools.vectorSubtraction(vertices[2], vertices[0]), Tools.vectorSubtraction(vertices[1], vertices[0]));
     			normalVectors[counter+1] = Tools.crossProduct(Tools.vectorSubtraction(vertices[0], vertices[1]), Tools.vectorSubtraction(vertices[3], vertices[1]));
				normalVectors[counter+2] = Tools.crossProduct(Tools.vectorSubtraction(vertices[3], vertices[2]), Tools.vectorSubtraction(vertices[0], vertices[2]));
				normalVectors[counter+3] = Tools.crossProduct(Tools.vectorSubtraction(vertices[1], vertices[3]), Tools.vectorSubtraction(vertices[2], vertices[3]));						

				counter = counter + 4;
			}
		}    	
    }
    
    /**
     * Renders a floor with tessellation
     * Tessellation is neccessary here because otherwise we would loose a spot light on it.
     */
    @Override
	public void render() {
		gl.glPushMatrix();
		gl.glEnable(GL.GL_TEXTURE_2D);
		
		//floor texture
		floorTexture.enable(gl);
		floorTexture.bind(gl);
		
		// basic color
		gl.glColor3f(FLOOR_COLOR[0], FLOOR_COLOR[1], FLOOR_COLOR[2]);

		for (int i = 0; i < vertices.length; i++) {
				if(i % 4 == 0) { gl.glBegin(GL2.GL_TRIANGLE_STRIP); } // each rectangle is made up of two triangles
				
					gl.glNormal3f(normalVectors[i][0], normalVectors[i][1], normalVectors[i][2]);
					gl.glTexCoord2f(textCoords[i][0], textCoords[i][1]);		
					gl.glVertex3f(vertices[i][0], vertices[i][1], vertices[i][2]);

				if(i % 4 == 3) { gl.glEnd(); }
		}
		
		gl.glDisable(GL.GL_TEXTURE_2D);		
		gl.glPopMatrix();
	}

}
