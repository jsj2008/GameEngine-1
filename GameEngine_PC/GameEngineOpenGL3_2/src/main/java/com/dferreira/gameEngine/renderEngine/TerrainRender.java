package com.dferreira.gameEngine.renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.dferreira.commons.ColorRGBA;
import com.dferreira.commons.GLTransformation;
import com.dferreira.commons.generic_render.IFrameRenderAPI;
import com.dferreira.commons.generic_render.IRawModel;
import com.dferreira.commons.models.Light;
import com.dferreira.commons.utils.Utils;
import com.dferreira.gameEngine.models.Terrain;
import com.dferreira.gameEngine.shaders.terrains.TTerrainAttribute;
import com.dferreira.gameEngine.shaders.terrains.TerrainShaderManager;
import com.dferreira.gameEngine.textures.TerrainTexturesPack;

/**
 * Class responsible to render the entities in the screen
 */
public class TerrainRender extends GenericRender {

	/**
	 * Reference to the shader manager
	 */
	private TerrainShaderManager tShader;

    /**
     * Constructor of the terrain render
     *
     * @param sManager         Shader manager
     * @param projectionMatrix The projection matrix of the render
     * @param frameRenderAPI   Reference to the API responsible for render the frame
     */
	public TerrainRender(TerrainShaderManager sManager, GLTransformation projectionMatrix, IFrameRenderAPI frameRenderAPI) {
        super(frameRenderAPI);
		this.tShader = sManager;

		sManager.start();
		sManager.loadProjectionMatrix(projectionMatrix);
		sManager.connectTextureUnits();
		sManager.stop();
	}

	/**
	 * Get the transformation matrix of one terrain
	 * 
	 * @param terrain
	 *            Entity for which is to create the transformation matrix
	 * 
	 * @return The transformation matrix that put the terrain in its right
	 *         position
	 */
	private GLTransformation getTransformationMatrix(Terrain terrain) {
		GLTransformation matrix = new GLTransformation();
		matrix.loadIdentity();
		matrix.translate(terrain.getX(), terrain.getY(), terrain.getZ());
		float terrainRotation = 0.0f;
		matrix.rotate(terrainRotation, 1.0f, 0.0f, 0.0f);
		matrix.rotate(terrainRotation, 0.0f, 1.0f, 0.0f);
		matrix.rotate(terrainRotation, 0.0f, 0.0f, 1.0f);

		return matrix;
	}

    /**
     * Render the terrains in the scene
     *
     * @param skyColor   Color of the sky
     * @param lights     The lights of the scene
     * @param viewMatrix View matrix to render the scene
     * @param terrains   List of terrains of the scene
     */
    public void render(ColorRGBA skyColor, Light[] lights, GLTransformation viewMatrix, List<Terrain> terrains) {
        tShader.start();
        tShader.loadSkyColor(skyColor);
        tShader.loadLights(lights);
        tShader.loadViewMatrix(viewMatrix);

        this.render(terrains);
        tShader.stop();
    }

    /**
     * Render one list of terrains
     *
     * @param terrains List of Terrains to render
     */
    private void render(List<Terrain> terrains) {
        if (!Utils.isEmpty(terrains)) {
            for (Terrain terrain : terrains) {
                prepareTerrain(terrain);
                prepareInstance(terrain);
                render(terrain);
                unbindTexturedModel();
            }
        }
    }
    
	/**
	 * Bind the several textures of the terrain
	 * 
	 * @param terrain
	 *            Terrain that is going to get the textures bound
	 */
	private void bindTextures(Terrain terrain) {
		TerrainTexturesPack texturesPackage = terrain.getTexturePack();
        this.frameRenderAPI.activeAndBindTextures(texturesPackage.getBackgroundTexture(),
                texturesPackage.getMudTexture(),
                texturesPackage.getGrassTexture(),
                texturesPackage.getPathTexture(),
                texturesPackage.getWeightMapTexture());
	}

	/**
	 * Bind the attributes of openGL
	 * 
	 * @param terrain
	 *            Terrain to get prepared
	 */
	private void prepareTerrain(Terrain terrain) {
		IRawModel model = terrain.getModel();

		// bind several textures of the terrain
		bindTextures(terrain);

		// Bind the light properties
		tShader.loadShineVariables(1.0f, 0.0f);
		
		this.frameRenderAPI.prepareModel(model,
                TTerrainAttribute.position,
                TTerrainAttribute.textureCoords,
                TTerrainAttribute.normal
        );
	}


	/**
	 * Render the terrain itself
	 * 
	 * @param terrain
	 *            the terrain to render
	 */
	private void prepareInstance(Terrain terrain) {
		// Load the transformation matrix
		tShader.loadTransformationMatrix(getTransformationMatrix(terrain));
	}

	/**
	 * Call the render of the triangles to the terrain itself
	 * 
	 * @param terrain
	 *            A reference to the terrain to get render
	 */
	private void render(Terrain terrain) {
		IRawModel model = terrain.getModel();
        this.frameRenderAPI.drawTrianglesIndexes(model);
	}

	/**
	 * UnBind the previous binded elements
	 */
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(TTerrainAttribute.position.getValue());
		GL20.glDisableVertexAttribArray(TTerrainAttribute.textureCoords.getValue());
		GL20.glDisableVertexAttribArray(TTerrainAttribute.normal.getValue());
		GL30.glBindVertexArray(0);
	}

	/**
	 * Clean up because we need to clean up when we finish the program
	 */
	@Override
	public void dispose() {
		tShader.dispose();
	}

}
