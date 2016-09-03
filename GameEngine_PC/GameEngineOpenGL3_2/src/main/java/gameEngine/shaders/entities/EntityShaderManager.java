package gameEngine.shaders.entities;

import java.util.HashMap;

import com.dferreira.commons.GLTransformation;
import com.dferreira.commons.Vector3f;
import com.dferreira.commons.models.Light;

import gameEngine.shaders.ShaderManager;
import gameEngine.shaders.terrains.TTerrainLocation;

/**
 * Manager of the shader files that are going to be load to render the 
 * 3D Entities
 */
public class EntityShaderManager extends ShaderManager {


	/**
	 * Path of the vertex shader file
	 */
	private static final String VERTEX_FILE = COMMON_PATH + "entities/entity_vertex_shader.glsl";

	/**
	 * Path of the fragment shader file
	 */
	private static final String FRAGMENT_FILE = COMMON_PATH + "entities/entity_fragment_shader.glsl";

	/**
	 * Position where to location attribute is going to be bind in the shader
	 * program
	 */
	public final static int LOCATION_ATTR_ID = 0;
	

	/**
	 * Position where to texture attribute is going to be bind in the program
	 * shader
	 */
	public final static int TEX_COORDINATE_ATTR_ID = 1;
	
	/**
	 * Position where the normal vector are going to be bind in the program
	 * shader
	 */
	public final static int NORMAL_VECTOR_ATTR_ID = 2;
	
	/**
	 * Id of attribute the position where the light of scene is
	 */
	public final static int LIGHT_POSITION_ATTR_ID = 2;
	
	/**
	 * Id of attribute the color where the light of scene have
	 */
	public final static int LIGHT_COLOR_ATTR_ID = 3;
	
	/**
	 * All the locations in the shader programs
	 */
	private int[] locations;

	
	/**
	 * Constructor of the game shader where the vertex and fragment shader of
	 * the game engine are loaded
	 */
	public EntityShaderManager() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	/**
	 * Bind the attributes to the program
	 * 
	 */
	@Override
	protected HashMap<Integer, String> getAttributes() {
		HashMap<Integer, String> attributes = new HashMap<>();
		attributes.put(LOCATION_ATTR_ID, "position");
		attributes.put(TEX_COORDINATE_ATTR_ID, "textureCoords");
		attributes.put(NORMAL_VECTOR_ATTR_ID, "normal");
		
		return attributes;
	}

	/**
	 * Get all the uniform location in the shader script
	 */
	@Override
	protected void getAllUniformLocations() {		
		int size = TEntityLocation.numOfEntityLocations.ordinal();
		locations = new int[size];
		
		for(int i = 0;i < size; i++ ) {
			TEntityLocation locationKey = TEntityLocation.values()[i];
			locations[i] = super.getUniformLocation(locationKey.toString());
		}
	}
	
	/**
	 * Load the color of the sky in order to simulate fog
	 * 
	 * @param skyColor
	 * 			Color of the sky
	 */
	public void loadSkyColor(Vector3f skyColor) {
		super.loadVector(locations[TTerrainLocation.skyColor.ordinal()], skyColor);
	}

	/**
	 * Load the projection matrix
	 * 
	 * @param matrix
	 *            the matrix to be loaded
	 */
	public void loadProjectionMatrix(GLTransformation matrix) {
		super.loadMatrix(locations[TEntityLocation.projectionMatrix.ordinal()], matrix);
	}
	
	/**
	 * Put passes the information of the light to the 
	 * Shader program
	 * 
	 * @param light the light to load in the shader program
	 */
	public void loadLight(Light light){
		super.loadVector(locations[TEntityLocation.lightPosition.ordinal()], light.getPosition());
		super.loadVector(locations[TEntityLocation.lightColor.ordinal()], light.getColor());
	}
	
	/**
	 * Load the values of the specular light in the fragment shader
	 * 
	 * @param damper		The damper of the specular lighting
	 * @param reflectivity	The reflectivity of the material
	 */
	public void loadShineVariables(float damper, float reflectivity) {
	    super.loadFloat(locations[TEntityLocation.shineDamper.ordinal()], damper);
	    super.loadFloat(locations[TEntityLocation.reflectivity.ordinal()] ,reflectivity);
	}
	
	/**
	 * Set in the shader if the normals should all of them point up
	 * 
 * @param normalsPointingUp Flag that indicates if all the normals of the entity are poiting up or not
	 */
	public void loadNormalsPointingUp(boolean normalsPointingUp) {
		super.loadBoolean(locations[TEntityLocation.normalsPointingUp.ordinal()], normalsPointingUp);
	}
	


	/**
	 * Load the view matrix
	 * 
	 * @param matrix
	 *            the matrix to be loaded
	 */
	public void loadViewMatrix(GLTransformation matrix) {
		super.loadMatrix(locations[TEntityLocation.viewMatrix.ordinal()], matrix);
	}

	/**
	 * Load the transformation matrix
	 * 
	 * @param matrix
	 *            the matrix to be loaded
	 */
	public void loadTransformationMatrix(GLTransformation matrix) {
		super.loadMatrix(locations[TEntityLocation.transformationMatrix.ordinal()], matrix);
	}

}
