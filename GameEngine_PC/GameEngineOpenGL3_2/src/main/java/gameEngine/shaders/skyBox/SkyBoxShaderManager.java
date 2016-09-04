package gameEngine.shaders.skyBox;

import java.util.HashMap;

import com.dferreira.commons.GLTransformation;

import gameEngine.shaders.ShaderManager;

/**
 * Manager of the shader files that are going to be load to render the 
 * 3D Terrain
 */
public class SkyBoxShaderManager extends ShaderManager {


	/**
	 * Path of the vertex shader file
	 */
	private static final String VERTEX_FILE = COMMON_PATH + "skyBox/sky_box_vertex_shader.glsl";

	/**
	 * Path of the fragment shader file
	 */
	private static final String FRAGMENT_FILE = COMMON_PATH + "skyBox/sky_box_fragment_shader.glsl";

	/**
	 * Position where to location attribute is going to be bind in the shader
	 * program
	 */
	public final static int LOCATION_ATTR_ID = 0;
	
	/**
	 * Location of the projection matrix in the program shader
	 */
	private int location_projectionMatrix;

	/**
	 * Location of the view matrix in the program shader
	 */
	private int location_viewMatrix;

		
	/**
	 * Constructor of the game shader where the vertex and fragment shader of
	 * the game engine are loaded
	 */
	public SkyBoxShaderManager() {
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
		
		return attributes;
	}

	/**
	 * Get all the uniform location in the shader script
	 */
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");	
	}
	

	/**
	 * Load the projection matrix
	 * 
	 * @param matrix
	 *            the matrix to be loaded
	 */
	public void loadProjectionMatrix(GLTransformation matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	/**
	 * Load the transformation matrix
	 * 
	 * @param matrix
	 *            the matrix to be loaded
	 */
	public void loadViewMatrix(GLTransformation matrix) {
		matrix.setTranslation(0.0f, 0.0f, 0.0f);
		super.loadMatrix(location_viewMatrix, matrix);
	}
}