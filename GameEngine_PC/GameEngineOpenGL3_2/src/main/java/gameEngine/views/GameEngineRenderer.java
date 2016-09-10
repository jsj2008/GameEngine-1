package gameEngine.views;

import java.util.Date;

import com.dferreira.commons.models.Light;

import gameEngine.modelGenerators.WorldEntitiesGenerator;
import gameEngine.modelGenerators.WorldSkyBoxGenerator;
import gameEngine.modelGenerators.WorldTerrainsGenerator;
import gameEngine.models.Entity;
import gameEngine.models.SkyBox;
import gameEngine.models.Terrain;
import gameEngine.renderEngine.DisplayManager;
import gameEngine.renderEngine.Loader;
import gameEngine.renderEngine.MasterRender;

public class GameEngineRenderer {

	/**
	 * Loader should handle the loading of resources from disk
	 */
	private Loader loader;

	/**
	 * The master render is going put all the elements together in order to
	 * render the scene
	 */
	private MasterRender renderer;

	/**
	 * Array of entities to render
	 */
	private Entity[] entities;

	/**
	 * Array of terrains to render
	 */
	private Terrain[] terrains;

	/**
	 * Position of the light in scene
	 */
	private Light light;

	/**
	 * SkyBox of the 3D world
	 */
	private SkyBox skyBox;

	/**
	 * Constructor of the game engine render
	 */
	public GameEngineRenderer() {
		super();
	}

	/**
	 * Initialize the shader programs objects and load the different components
	 * of the application
	 *
	 */
	public void onSurfaceCreated() {
		/* Initializes the main variables responsible to render the 3D world */
		this.loader = new Loader();
		this.renderer = new MasterRender();

		/* Prepares the entities that is going to be render */
		this.entities = WorldEntitiesGenerator.getEntities(loader);

		/* Prepares the terrains that is going to render */
		this.terrains = WorldTerrainsGenerator.getTerrains(loader);

		/* Load the light that is going to render */
		this.light = WorldEntitiesGenerator.getLight();

		/* Load the sky box that is going to render */
		this.skyBox = WorldSkyBoxGenerator.getSky(loader);
	}

	/**
	 * Draw the entities of the scene
	 *
	 */
	public void onDrawFrame() {
		Date startDate = new Date();

		// game logic
		renderer.processTerrains(terrains);
		renderer.processEntities(entities);
		renderer.processSkyBox(skyBox);

		renderer.render(light);

		DisplayManager.updateDisplay();

		// Logs frames/s
		Date endDate = new Date();
		long timeToRender = (endDate.getTime() - startDate.getTime());
		System.out.println((1000.0f / timeToRender) + "Frames/s");
	}

	/**
	 * Called when is to release resources used
	 */
	public void dealloc() {
		this.loader.cleanUp();
		this.renderer.cleanUp();
	}
}