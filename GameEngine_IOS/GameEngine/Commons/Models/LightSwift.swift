import Foundation

/**
* Represents one light source in the scene
*/
public class Light : NSObject {
    
    /**
    * Position where the light will exist
    */
    var position : Vector3f;
    
    /**
    * The intensity of the light in the different components
    */
    var color : Vector3f;
    
    /**
    * The constructor of the light entity
    *
    * @param aPosition
    *            Position where the light will exist
    * @param aColor
    *            The intensity of the light in the different components
    */
    public init(position : Vector3f, color : Vector3f) {
        self.position = position
        self.color = color
    }
    
}
