package com.dferreira.game_engine.shaders;

import android.content.Context;
import android.util.Log;

import com.dferreira.commons.ColorRGB;
import com.dferreira.commons.ColorRGBA;
import com.dferreira.commons.GLTransformation;
import com.dferreira.commons.IEnum;
import com.dferreira.commons.generic_render.IShaderManagerAPI;
import com.dferreira.commons.generic_render.ShaderProgram;
import com.dferreira.commons.Vector3f;
import com.dferreira.commons.utils.LoadUtils;

import java.util.List;

/**
 * Generic shader manager with methods to load vertex shader and fragment shader files
 * as well as useful methods to bind arguments
 */
public abstract class ShaderManager {

    private static final String TAG = "ShaderManager";
    private final ShaderProgram shaderProgram;
    private final IShaderManagerAPI shaderManagerAPI;


    /**
     * Constructor of the program shader manager
     *
     * @param context       Context where the game engine will be created
     * @param vertexFile    Identifier of the file with vertex description
     * @param fragmentFile  Identifier of the file with fragment description
     * @param shaderManagerAPI     Reference to the API that is going to manage the program shader
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    protected ShaderManager(Context context, int vertexFile, int fragmentFile, IShaderManagerAPI shaderManagerAPI) {

        String vertexShaderSrc = LoadUtils.readTextFromRawResource(context, vertexFile);
        String fragShaderSrc = LoadUtils.readTextFromRawResource(context, fragmentFile);
        this.shaderManagerAPI = shaderManagerAPI;
        this.shaderProgram = shaderManagerAPI.loadProgram(vertexShaderSrc, fragShaderSrc);

        if (this.shaderProgram == null) {
            Log.e(TAG, "Was impossible compile the program shader");
            return;
        }
        bindAttributes();
        boolean linked = shaderManagerAPI.linkProgram(shaderProgram);
        if (linked) {
            getAllUniformLocations();
        }
    }

    /**
     * Called to bind the attributes to the program shader
     */
    private void bindAttributes() {
        List<IEnum> attributes = getAttributes();
        if ((attributes != null) && (!attributes.isEmpty())) {
            for (IEnum attribute : attributes) {
                bindAttribute(attribute.getValue(), attribute.toString());
            }
        }
    }

    /**
     * Called to bind the attributes to the program shader
     */
    protected abstract List<IEnum> getAttributes();

    /**
     * Called to ensure that all the shader managers get their uniform locations
     */
    protected abstract void getAllUniformLocations();

    /**
     * Bind one attribute
     *
     * @param attributeIndex Index of the attribute to bind
     * @param variableName   Name of the attribute to bind
     */
    private void bindAttribute(int attributeIndex, String variableName) {
        this.shaderManagerAPI.glBindAttributeLocation(shaderProgram, attributeIndex, variableName);
    }

    /**
     * Get the position of one uniform variable in the program shader
     *
     * @param uniformName the name of the uniform variable as appears in the shader code
     * @return the position of the uniform variable in program shader
     */
    protected int getUniformLocation(Enum<?> uniformName) {
        int location = this.shaderManagerAPI.getUniformLocation(shaderProgram, uniformName);
        if (location < 0) {
            Log.e(TAG, "Was not possible to load the location : " + uniformName);
        }
        return location;
    }

    /**
     * Load a integer value to be used in the shader script
     *
     * @param location location of the shader variable in the script
     * @param value    The value to load
     */
    protected void loadInt(int location, int value) {
        this.shaderManagerAPI.loadInt(location, value);
    }

    /**
     * Load a float value to be used in the shader script
     *
     * @param location location of the shader variable in the script
     * @param value    value to load
     */
    protected void loadFloat(int location, float value) {
        this.shaderManagerAPI.loadFloat(location, value);
    }


    /**
     * Load a 3D vector to be used in the shader script
     *
     * @param location location of the shader variable in the script
     * @param vector   The vector to load
     */
    protected void loadVector(int location, Vector3f vector) {
        this.shaderManagerAPI.loadVector(location, vector);
    }

    /**
     * Load a color RGB to be used in the shader script
     *
     * @param location location of the shader variable in the script
     * @param color    The color to load
     */
    protected void loadColorRGB(int location, ColorRGB color) {
        this.shaderManagerAPI.loadColorRGB(location, color);
    }

    /**
     * Load a color RGBA to be used in the shader script
     *
     * @param location location of the shader variable in the script
     * @param color    The color to load
     */
    protected void loadColorRGBA(int location, ColorRGBA color) {
        this.shaderManagerAPI.loadColorRGBA(location, color);
    }

    /**
     * Load a boolean value to be used in the shader script
     *
     * @param location The location of the shader variable in the script
     * @param value    value to load
     */
    protected void loadBoolean(int location, boolean value) {
        this.shaderManagerAPI.loadBoolean(location, value);
    }

    /**
     * Load a matrix to be used in the shader script
     *
     * @param location The location of the shader variable in the script
     * @param matrix   Matrix to load
     */
    protected void loadMatrix(int location, GLTransformation matrix) {
        this.shaderManagerAPI.loadMatrix(location, matrix);
    }


    /**
     * Indicates that should start to use a certain program shader
     */
    public void start() {
        this.shaderManagerAPI.start(shaderProgram);
    }

    /**
     * Indicate that should not use a certain program no more
     */
    public void stop() {
        this.shaderManagerAPI.stop();
    }

    /**
     * Clean the program shader from memory
     */
    public void cleanUp() {
        this.shaderManagerAPI.deleteProgram(shaderProgram);
    }
}
