import Foundation

/**
* Uniforms in the shader program of the entities
*/
public enum TEntityUniform : Int {
    case
    
    /**
    * Location of the projection matrix in the program shader
    */
    projectionMatrix = 0,
    
    /**
    * Location of the view matrix in the program shader
    */
    viewMatrix,
    
    /**
    * Location of the transformation matrix in the program shader
    */
    transformationMatrix,
    
    /**
    * Location of the light's position in the program shader
    */
    lightPosition,
    
    /**
    * Location of the light's color in the program shader
    */
    lightColor,
    
    /**
    * Location of the shineDamper uniform in the fragment shader
    */
    shineDamper,
    
    /**
    * Location of the reflectivity uniform in the fragment shader
    */
    reflectivity,
    
    /**
    * Color of the sky in order to simulate fog
    */
    skyColor,
    
    /**
    * Location of the variable that indicates the normals of the object should
    * point up
    */
    normalsPointingUp,
    
    /**
     * Weight of the texture in the final ambient color to be render
     */
    textureWeight,
    
    /**
     * Color that the material is going to get if there is not texture
     */
    diffuseColor,
    
    /**
    * Meta-data used maintain the existing locations
    */
    numOfEntityLocations;
}
