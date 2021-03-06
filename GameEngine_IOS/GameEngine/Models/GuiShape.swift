import Foundation

/**
 * Represents the 2D GUI
 */
open class GuiShape : NSObject, IShape {
    fileprivate var _vertices : UnsafeMutablePointer<Float>? ;
    
    open static let SIZE : Float = 1.0
    fileprivate let GUI_NUMBER_OF_ELEMENTS : Int = 8;
    
    /**
     * Vertices of the GUI quad
     */
    fileprivate let guiVertexData : Array<Float> = [
        -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, SIZE, -SIZE
    ];
    
    
    /**
     * Initiator of the GUI shape
     */
    public override init() {
        //Allocate and fill the vertices memory
        self._vertices = UnsafeMutablePointer<Float>.allocate(capacity: guiVertexData.count);
        
        //Copy vertices one by one
        for i in 0 ..< guiVertexData.count {
            self._vertices![i] = guiVertexData[i];
        }
    }
    
    /**
     * Deinitialization of the gui shape
     */
    deinit {
        if(self._vertices != nil)
        {
            free(self._vertices!);
            self._vertices = nil;
        }
    }
    
    
    /**
     *
     * @return The vertices of the quad
     */
    open func getVertices() -> UnsafeMutablePointer<Float>? {
        return self._vertices;
    }
    
    /**
     * @return number of vertices that make the shape
     */
    open func countVertices() -> Int {
        return GUI_NUMBER_OF_ELEMENTS;
    }
    
    /**
     * @return the Coordinates of the textures of the shape
     */
    open func getTextureCoords()  -> UnsafeMutablePointer<Float>? {
        return nil;
    }
    
    /**
     *Number of the texture coordinates
     */
    open func countTextureCoords() -> Int {
        return 0;
    }
    
    /**
     * @return the normal vectors that make the shape
     */
    open func getNormals() -> UnsafeMutablePointer<Float>? {
        return nil;
    }
    
    /*
     * Number of normal that the shape has
     */
    open func countNormals() -> Int {
        return 0;
    }
    
    /**
     * @return The indices of the vertices that make the shape
     */
    open func getIndices() -> UnsafeMutablePointer<ushort>? {
        return nil;
    }
    
    /*
     Number of indices that the shape has
     */
    open func countIndices() -> Int {
        return 0;
    }
    
    /**
     * @return the groupName Name of the group wish belongs
     */
    open func getGroupName() -> String? {
        return nil;
    }
    
    /**
     *
     * @return The material that is associated with shape
     */
    open func getMaterial() -> IExternalMaterial? {
        return nil;
    }
}
