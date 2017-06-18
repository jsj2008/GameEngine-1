package com.dferreira.gameEngine.engineTester;

import com.dferreira.gameEngine.audioEngine.AudioManager;
import com.dferreira.gameEngine.renderEngine.DisplayManager;
import com.dferreira.gameEngine.views.GameEngineRenderer;

/**
 * The entry point of the application
 *
 */
public class MainGameLoop {

	/**
	 * The main method of the application that is going to be run
	 * 
	 * @param args
	 *            the arguments passed to the application
	 */
	public static void main(String[] args) {
		DisplayManager.createDisplay();
		DisplayManager.printSystemInfo();
		AudioManager.init();

		GameEngineRenderer gameEngineRender = new GameEngineRenderer();

		gameEngineRender.onSurfaceCreated();
		while (DisplayManager.closeWasNotRequested()) {
			gameEngineRender.onDrawFrame();
		}
		gameEngineRender.dealloc();

		/* Calls the clean up methods to free memory */

		DisplayManager.closeDisplay();
		AudioManager.cleanUp();
	}
}