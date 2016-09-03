package gameEngine.renderEngine;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.dferreira.commons.GLTransformation;
import com.dferreira.commons.models.Light;

import gameEngine.models.RawModel;
import gameEngine.models.Terrain;
import gameEngine.shaders.terrains.TerrainShaderManager;
import gameEngine.textures.TerrainTexturesPack;

public class TerrainRender {
	/**
	 * Reference to the shader manager
	 */
	private TerrainShaderManager tShader;
	
	/**
	 * Constructor of the entity render
	 * 
	 * @param sManager
	 *            Shader manager
	 */
	public TerrainRender(TerrainShaderManager sManager, GLTransformation projectionMatrix) {
		this.tShader = sManager;

		sManager.start();
		sManager.loadProjectionMatrix(projectionMatrix);
		sManager.connectTextureUnits();
		sManager.stop();
	}
	
	/**
	 * Get the transformation matrix of one entity
	 * 
	 * @param entity
	 *            Entity for which is to create the transformation matrix
	 * 
	 * @return The transformation matrix that put the entity in its right
	 *         position
	 */
	private GLTransformation getTransformationMatrix(Terrain entity) {
		GLTransformation matrix = new GLTransformation();
		matrix.glLoadIdentity();
		matrix.glTranslate(entity.getX(), entity.getY(), entity.getZ());
		float terrainRotation = 0.0f;
		matrix.glRotate(terrainRotation, 1.0f, 0.0f, 0.0f);
		matrix.glRotate(terrainRotation, 0.0f, 1.0f, 0.0f);
		matrix.glRotate(terrainRotation, 0.0f, 0.0f, 1.0f);

		return matrix;
	}
	
	/**
	 * Render the terrains in the scene
	 * 
	 * @param sun
	 *            The source of light of the scene
	 * @param viewMatrix
	 *            View matrix to render the scene
	 * @param terrains
	 *            List of terrains of the scene
	 */
	public void render(Light sun, GLTransformation viewMatrix, List<Terrain> terrains) {
		tShader.start();
		tShader.loadLight(sun);
		tShader.loadViewMatrix(viewMatrix);

		this.render(terrains);
		tShader.stop();
	}
	
	/**
	 * Render one hashMap of entities where each key is a group of similar
	 * entities to be render
	 * 
	 * @param terrains
	 *            HashMap of entities to render
	 */
	private void render(List<Terrain> terrains) {
		if ((terrains == null) || (terrains.size() == 0)) {
			return;
		} else {
			for (Terrain terrain : terrains) {
				prepareTerrain(terrain);
				prepareInstance(terrain);
				render(terrain);
				unbindTexturedModel();
			}
		}
	}
	
	/**
	 * Bind the attributes of terrain with openGL
	 * 
	 * @param terrain The terrain that have the properties to bind
	 */
	private void prepareTerrain(Terrain terrain) {
		RawModel model = terrain.getModel();
		
		//Enable the attributes to bind
		GL20.glEnableVertexAttribArray(TerrainShaderManager.LOCATION_ATTR_ID);
		GL20.glEnableVertexAttribArray(TerrainShaderManager.TEX_COORDINATE_ATTR_ID);
		GL20.glEnableVertexAttribArray(TerrainShaderManager.NORMAL_VECTOR_ATTR_ID);
		
		// bind several textures of the terrain
		bindTextures(terrain);
		
		//Load the light properties
		tShader.loadShineVariables(1.0f, 0.0f);
		
		//Load from buffers
		// Load the vertex data
		GL20.glVertexAttribPointer(TerrainShaderManager.LOCATION_ATTR_ID, RenderConstants.VERTEX_SIZE, RenderConstants.VERTEX_NORMALIZED, RenderConstants.STRIDE,
				model.getVertexBuffer());
		// Load the texture coordinate
		GL20.glVertexAttribPointer(TerrainShaderManager.TEX_COORDINATE_ATTR_ID, RenderConstants.NUMBER_COMPONENTS_PER_VERTEX_ATTR,
				RenderConstants.VERTEX_NORMALIZED, RenderConstants.STRIDE, model.getTexCoordinates());
		// Load the normals data
		GL20.glVertexAttribPointer(TerrainShaderManager.NORMAL_VECTOR_ATTR_ID, RenderConstants.NUMBER_COMPONENTS_PER_NORMAL_ATTR, RenderConstants.VERTEX_NORMALIZED, RenderConstants.STRIDE,
				model.getNormalBuffer());
	}
	
    /**
     * When loads one texture defines that by default should zoom in/out it
     */
    private void defineTextureFunctionFilters() {
        //The texture minifying function is used whenever the pixel being textured maps to an area greater than one texture element
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        //The texture magnification function is used when the pixel being textured maps to an area less than or equal to one texture element
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        //Sets the wrap parameter for texture coordinate s
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);

        //Sets the wrap parameter for texture coordinate t
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    }
	
	/**
	 * Bind the several textures of the terrain
	 */
	private void bindTextures(Terrain terrain) {
		TerrainTexturesPack texuresPackage = terrain.getTexturePack();
		HashMap<Integer, Integer> texturesMatching = new HashMap<Integer, Integer>();
		texturesMatching.put(GL13.GL_TEXTURE0, texuresPackage.getBackgroundTextureId());
		texturesMatching.put(GL13.GL_TEXTURE1, texuresPackage.getMudTextureId());
		texturesMatching.put(GL13.GL_TEXTURE2, texuresPackage.getGrassTextureId());
		texturesMatching.put(GL13.GL_TEXTURE3, texuresPackage.getPathTextureId());
		texturesMatching.put(GL13.GL_TEXTURE4, texuresPackage.getWeightMapTextureId());

		for (int key : texturesMatching.keySet()) {
			GL13.glActiveTexture(key);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturesMatching.get(key));
			
			// Set filtering of the texture
            defineTextureFunctionFilters();
		}
	}
	
	/**
	 * Render the terrain itself
	 * 
	 * @param terrain the terrain to render
	 */
	private void prepareInstance(Terrain terrain) {
		// Load the transformation matrix
		tShader.loadTransformationMatrix(getTransformationMatrix(terrain));
	}
	
	/**
	 * Call the render of the triangles to the entity itself
	 * 
	 * @param terrain
	 */
	private void render(Terrain terrain) {
		RawModel model = terrain.getModel();

		// Specify the indexes
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getIndexBuffer());
	}
	
	/**
	 * UnBind the previous binded elements
	 */
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(TerrainShaderManager.LOCATION_ATTR_ID);
		GL20.glDisableVertexAttribArray(TerrainShaderManager.TEX_COORDINATE_ATTR_ID);
		GL20.glDisableVertexAttribArray(TerrainShaderManager.NORMAL_VECTOR_ATTR_ID);
	}
	
	/**
	 * Clean up because we need to clean up when we finish the program
	 */
	public void cleanUp() {
		tShader.cleanUp();
	}
}
