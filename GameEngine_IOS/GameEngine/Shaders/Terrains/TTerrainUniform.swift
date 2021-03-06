import Foundation

/**
* Locations in the shader program of the terrains
*/
public enum TTerrainUniform : Int {
    case
    
    /**
    * Location of the transformation matrix in the program shader
    */
    transformationMatrix = 0,
    
    /**
    * Location of the view matrix in the program shader
    */
    viewMatrix,
    
    /**
    * Location of the projection matrix in the program shader
    */
    projectionMatrix,
    
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
    * Location of the color of the sky in the fragment shader
    */
    skyColor,
    
    /**
    * The background texture
    */
    backgroundTexture,
    
    /**
    * The mud texture
    */
    mudTexture,
    
    /**
    * The grass texture
    */
    grassTexture,
    
    /**
    * The path texture
    */
    pathTexture,
    
    /**
    * The blend map texture
    */
    weightMapTexture,
    
    /**
    * Meta-data used maintain the existing locations
    */
    numOfTerrainLocations;
}