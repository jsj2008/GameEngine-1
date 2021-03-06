package com.dferreira.gameEngine.shaders.terrains;

import com.dferreira.commons.IEnum;

/**
 * Enumeration of attributes to the terrain shader
 */
public enum TTerrainAttribute implements IEnum {
    /**
     * Position where to location attribute is going to be bind in the shader
     * program
     */
    position,

    /**
     * Position where to texture attribute is going to be bind in the program
     * shader
     */
    textureCoords,

    /**
     * Position where the normal vector are going to be bind in the program
     * shader
     */
    normal,

    /**
     * Id of attribute the position where the light of scene is
     */
    lightPosition,

    /**
     * Id of attribute the color where the light of scene have
     */
    lightColor;

    /**
     * The value of the enumeration
     */
    @Override
    public int getValue() {
        return this.ordinal();
    }
}
