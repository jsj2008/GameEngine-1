package com.dferreira.game_engine.renderEngine;

import android.content.Context;
import android.opengl.GLES20;

import com.dferreira.commons.utils.LoadUtils;
import com.dferreira.commons.models.TextureData;
import com.dferreira.game_engine.models.RawModel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Load the data to render the scene
 */
public class Loader {

    @SuppressWarnings("FieldCanBeLocal")
    private final int INT_IN_BYTES = 4;

    @SuppressWarnings("FieldCanBeLocal")
    private final int FLOAT_IN_BYTES = 4;

    /**
     * Load to a new vertex array object
     *
     * @param positions          Positions to load
     * @param textureCoordinates Texture coordinates to load
     * @param normals            Normals of the model to load
     * @param indices            Indices to be load
     * @return A row model with information loaded
     */
    public RawModel loadToRawModel(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {

        FloatBuffer vertexBuffer = storeDataInFloatBuffer(positions);
        IntBuffer indexBuffer = storeDataInIntBuffer(indices);
        FloatBuffer normalBuffer = storeDataInFloatBuffer(normals);
        FloatBuffer texCoordinatesBuffer = storeDataInFloatBuffer(textureCoordinates);

        return new RawModel(vertexBuffer, indexBuffer, indices.length, normalBuffer, texCoordinatesBuffer);
    }

    /**
     * Load to a new vertex array object
     *
     * @param positions  Positions to load
     * @param dimensions Dimensions of the positions to load
     */
    private RawModel loadPositionsToRawModel(float[] positions, int dimensions) {
        FloatBuffer vertexBuffer = storeDataInFloatBuffer(positions);
        return new RawModel(vertexBuffer, positions.length / dimensions);
    }

    /**
     * Load a list of 3D positions to RawModel
     *
     * @param positions Positions to load
     * @return The rawModel pointing to the positions
     */
    public RawModel load3DPositionsToRawModel(float[] positions) {
        int dimensions = 3;
        return loadPositionsToRawModel(positions, dimensions);
    }

    /**
     * When loads one texture defines that by default should zoom in/out it
     *
     * @param target the target of the filter
     */
    private void defineTextureFunctionFilters(int target) {
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    /**
     * Load texture from resource
     *
     * @param context    Context where this method will be called
     * @param resourceId id of the resource where the texture exists
     * @return Id from the texture that was bounded in openGL
     */
    @SuppressWarnings("SameParameterValue")
    public int loadTexture(Context context, int resourceId) {
        TextureData textureData = LoadUtils.decodeTextureFile(context, resourceId);

        int[] textureId = new int[1];
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, textureData.getWidth(), textureData.getHeight(), 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, textureData.getBuffer());


        defineTextureFunctionFilters(GLES20.GL_TEXTURE_2D);

        return textureId[0];
    }

    /**
     * Loads a cubic texture
     *
     * @param context     Context where this method will be called
     * @param resourceIds The resources where should get the images of the cube
     * @return Identifier of the texture cubic texture loaded
     */
    public Integer loadTCubeMap(Context context, int[] resourceIds) {
        if (resourceIds == null) {
            return null;
        }
        int[] cubicTextureTargets = new int[]{GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z};

        int[] textureId = new int[1];
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId[0]);

        for (int i = 0; i < cubicTextureTargets.length; i++) {
            int resourceId = resourceIds[i];

            TextureData textureData = LoadUtils.decodeTextureFile(context, resourceId);
            if (textureData == null) {
                return null;
            } else {
                int target = cubicTextureTargets[i];
                GLES20.glTexImage2D(target, 0, GLES20.GL_RGBA, textureData.getWidth(), textureData.getHeight(), 0,
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, textureData.getBuffer());
            }
        }
        defineTextureFunctionFilters(GLES20.GL_TEXTURE_CUBE_MAP);
        return textureId[0];
    }

    /**
     * Convert on array of Integers in a buffer of Integers that can be used in openGL
     *
     * @param data
     * @return
     */
    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer sBuffer = ByteBuffer.allocateDirect(data.length * INT_IN_BYTES)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        sBuffer.put(data).position(0);
        return sBuffer;
    }


    /**
     * Gets one array of float and reserves space to save it natively
     *
     * @param data data to be stored
     * @return
     */
    private FloatBuffer storeDataInFloatBuffer(float[] data) {

        // Allocate a direct block of memory on the native heap,
        // size in bytes is equal to data.length * BYTES_PER_FLOAT.
        // BYTES_PER_FLOAT is equal to 4, since a float is 32-bits, or 4 bytes.
        FloatBuffer fBuffer = ByteBuffer.allocateDirect(data.length * FLOAT_IN_BYTES)
                // Floats can be in big-endian or little-endian order.
                // We want the same as the native platform.
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        fBuffer
                // Copy data from the Java heap to the native heap.
                .put(data)
                // Reset the buffer position to the beginning of the buffer.
                .position(0);

        return fBuffer;
    }
}