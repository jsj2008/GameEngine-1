package com.dferreira.gameEngine.gl_render;

import android.opengl.GLES10;
import android.opengl.GLES20;

import com.dferreira.commons.IEnum;
import com.dferreira.commons.generic_render.IFrameRenderAPI;
import com.dferreira.commons.generic_render.IRawModel;
import com.dferreira.commons.generic_render.ITexture;
import com.dferreira.commons.generic_render.RenderAttributeEnum;
import com.dferreira.commons.generic_render.RenderConstants;

/**
 * Contains useful methods when is render on frame using OpenGL
 */

class GLFrameRender implements IFrameRenderAPI {

    /**
     * Clear the screen as well as the depth buffer
     */
    @Override
    public void prepareFrame() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Set the color clear value
        GLES20.glClearColor(0, 0.3f, 0, 1);

        // Clear the color and depth buffers
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }



    /**
     * Prepares one model to be render in scene
     *
     * @param model The model to be prepared to be rendered
     */
    @Override
    public void prepareModel(IRawModel model) {
        GLRawModel rawModel = (GLRawModel) model;

        IEnum position = rawModel.getAttribute(RenderAttributeEnum.position); // The position attribute
        IEnum textureCoords = rawModel.getAttribute(RenderAttributeEnum.textureCoords); // The texture attribute
        IEnum normal = rawModel.getAttribute(RenderAttributeEnum.normal);// The normal attribute position


        // Load the vertex data
        GLES20.glVertexAttribPointer(position.getValue(), RenderConstants.VERTEX_SIZE, GLES20.GL_FLOAT, RenderConstants.VERTEX_NORMALIZED, RenderConstants.STRIDE, rawModel.getVertexBuffer());

        // Load the texture coordinate
        GLES20.glVertexAttribPointer(textureCoords.getValue(), RenderConstants.NUMBER_COMPONENTS_PER_VERTEX_ATTR, GLES20.GL_FLOAT,
                RenderConstants.VERTEX_NORMALIZED,
                RenderConstants.STRIDE,
                rawModel.getTexCoordinates());


        // Load the normals data
        GLES20.glVertexAttribPointer(normal.getValue(), RenderConstants.NUMBER_COMPONENTS_PER_NORMAL_ATTR, GLES20.GL_FLOAT, RenderConstants.VERTEX_NORMALIZED, RenderConstants.STRIDE,
                rawModel.getNormalBuffer());

        //Enable the attributes to bind
        GLES20.glEnableVertexAttribArray(position.getValue());
        GLES20.glEnableVertexAttribArray(textureCoords.getValue());
        GLES20.glEnableVertexAttribArray(normal.getValue());
    }

    /**
     * Prepares one 2D model to be render in scene
     *
     * @param model The model to be prepared to be rendered
     */
    @Override
    public void prepare2DModel(IRawModel model) {
        GLRawModel rawModel = (GLRawModel) model;

        IEnum position = rawModel.getAttribute(RenderAttributeEnum.position); // The position attribute

        // Load the vertex data
        GLES20.glVertexAttribPointer(position.getValue(), RenderConstants.VERTEX_SIZE_2D, GLES20.GL_FLOAT, RenderConstants.VERTEX_NORMALIZED, RenderConstants.STRIDE, rawModel.getVertexBuffer());

        //Enable the attributes to bind
        GLES20.glEnableVertexAttribArray(position.getValue());


    }

    /**
     * Prepares one model to be render in scene
     *
     * @param model The model to be prepared to be rendered
     */
    @Override
    public void prepare3DModel(IRawModel model) {
        GLRawModel rawModel = (GLRawModel) model;

        IEnum position = rawModel.getAttribute(RenderAttributeEnum.position); // The position attribute

        // Load the vertex data
        GLES20.glVertexAttribPointer(position.getValue(), RenderConstants.VERTEX_SIZE, GLES20.GL_FLOAT, RenderConstants.VERTEX_NORMALIZED, RenderConstants.STRIDE, rawModel.getVertexBuffer());


        //Enable the attributes to bind
        GLES20.glEnableVertexAttribArray(position.getValue());
    }

    /**
     * UnBind the previous bound elements
     *
     * @param model The model to be prepared to be rendered
     */
    @Override
    public void unPrepareModel(IRawModel model) {
        GLRawModel rawModel = (GLRawModel) model;

        IEnum position = rawModel.getAttribute(RenderAttributeEnum.position); // The position attribute
        IEnum textureCoords = rawModel.getAttribute(RenderAttributeEnum.textureCoords); // The texture attribute
        IEnum normal = rawModel.getAttribute(RenderAttributeEnum.normal);// The normal attribute position


        GLES20.glDisableVertexAttribArray(position.getValue());

        if (textureCoords != null) {
            GLES20.glDisableVertexAttribArray(textureCoords.getValue());
        }
        if (normal != null) {
            GLES20.glDisableVertexAttribArray(normal.getValue());
        }
    }

    /**
     * Activates and binds the texture with ID passed in the specified target
     *
     * @param target  Target where is to bind the texture
     * @param texture The texture to use
     */
    private void activeAndBind2DTexture(int target, ITexture texture) {
        GLTexture glTexture = (GLTexture) texture;

        //Enable the specific texture
        GLES20.glActiveTexture(target);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glTexture.getId());
    }

    /**
     * Activates and binds the texture with passed
     *
     * @param texture The texture to use
     */
    @Override
    public void activeAndBindTexture(ITexture texture) {
        //Enable the specific texture
        activeAndBind2DTexture(GLES20.GL_TEXTURE0, texture);
    }

    /**
     * Activates and binds a cubic texture with ID passed
     *
     * @param texture The texture to use
     */
    @Override
    public void activeAndBindCubeTexture(ITexture texture) {
        GLTexture glTexture = (GLTexture) texture;

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, glTexture.getId());
    }

    /**
     * Activates and binds a set of textures passed
     *
     * @param texture1 The texture 1
     * @param texture2 The texture 2
     * @param texture3 The texture 3
     * @param texture4 The texture 4
     * @param texture5 The texture 5
     */
    @Override
    public void activeAndBindTextures(ITexture texture1, ITexture texture2, ITexture texture3, ITexture texture4, ITexture texture5) {
        activeAndBind2DTexture(GLES20.GL_TEXTURE0, texture1);
        activeAndBind2DTexture(GLES20.GL_TEXTURE1, texture2);
        activeAndBind2DTexture(GLES20.GL_TEXTURE2, texture3);
        activeAndBind2DTexture(GLES20.GL_TEXTURE3, texture4);
        activeAndBind2DTexture(GLES20.GL_TEXTURE4, texture5);
    }

    /**
     * Draw a set of triangles using for that there indexes
     *
     * @param model The model to be drawn
     */
    @Override
    public void drawTrianglesIndexes(IRawModel model) {
        GLRawModel rawModel = (GLRawModel) model;

        //Specify the indexes
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, rawModel.getNumOfIndexes(),
                GLES20.GL_UNSIGNED_INT, rawModel.getIndexBuffer());
    }

    /**
     * Draw a set of triangles using for that there vertex
     *
     * @param model The model to be drawn
     */
    @Override
    public void drawTrianglesVertex(IRawModel model) {
        GLRawModel rawModel = (GLRawModel) model;

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, rawModel.getVertexCount());
    }

    /**
     * Draw a quad using for that there vertex
     *
     * @param quad The model to be drawn
     */
    @Override
    public void drawQuadVertex(IRawModel quad) {
        GLRawModel rawModel = (GLRawModel) quad;

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, rawModel.getVertexCount());
    }

    /**
     * Enable culling of faces to get better performance
     */
    @Override
    public void enableCulling() {
        // Enable the GL cull face feature
        GLES10.glEnable(GLES10.GL_CULL_FACE);
        // Avoid to render faces that are away from the camera
        GLES10.glCullFace(GLES10.GL_BACK);
    }

    /**
     * Disable the culling of the faces vital for transparent model
     */
    @Override
    public void disableCulling() {
        GLES10.glDisable(GLES10.GL_CULL_FACE);
    }

    /**
     * Enable the test of the depth will render one element in the screen
     */
    @Override
    public void enableDepthTest() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    /**
     * Disable the test of the depth will render one element in the screen
     */
    @Override
    public void disableDepthTest() {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    /**
     * Enable the blend of colors taking in account the alpha component of the color
     */
    @Override
    public void enableBlend() {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Disable the blend of colors taking in account the alpha component of the color
     */
    @Override
    public void disableBlend() {
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    /**
     * Specifies the transformation of x and y from normalized device coordinates to window coordinates
     *
     * @param x      Specify the lower left corner of the viewport rectangle, in pixels. The initial value is (0,0).
     * @param y      Specify the lower left corner of the viewport rectangle, in pixels. The initial value is (0,0).
     * @param width  Specify the width and height of the viewport. When a GL context is first attached to a window, width and height are set to the dimensions of that window.
     * @param height Specify the width and height of the viewport. When a GL context is first attached to a window, width and height are set to the dimensions of that window.
     */
    @Override
    public void setViewPort(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
    }

    /**
     * Clean up the resources used by the render API
     */
    @Override
    public void dispose() {
    }
}
