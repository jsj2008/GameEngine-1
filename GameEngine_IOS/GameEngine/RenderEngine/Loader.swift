import Foundation
import GLKit
import OpenAL
import AudioToolbox

/*
 * Load the elements to make the scene
 */
public class Loader : NSObject {
    
    func BUFFER_OFFSET(i: Int) -> UnsafePointer<Void> {
        let p: UnsafePointer<Void> = nil
        return p.advancedBy(i)
    }
    
    /**
     * List of the vertex array objects loaded
     */
    private var vaos : Array<GLuint>!;
    
    /**
     * List of the vertex buffer objects loaded
     */
    private var vbos : Array<GLuint>!;
    
    /**
     * List of the textures that make part of the game engine
     */
    private var textures : Array<GLuint>!;
    
    /**
     * Extension of the png files
     */
    private let PNG_EXTENSION : String  = "png";
    
    private let NUMBER_CUBE_FACES : Int = 6;
    
    /**
     * Initiator of the loader
     */
    public override init() {
        self.vaos = Array<GLuint>();
        self.vbos = Array<GLuint>();
        self.textures = Array<GLuint>();
    }
    
    /**
     *
     * @return If possible one audio source with everything set
     */
    private func genAudioSource() -> AudioSource! {
        var sourceId : ALuint = 0;
        
        alGenSources(1, &sourceId)
        let error = alGetError()
        if(error == AL_NO_ERROR)
        {
            return AudioSource(sourceId: sourceId)
        } else
        {
            return nil
        }
    }
    
    /**
     *
     * @param numberOfSources
     *            Number of audio sources to generate
     *
     * @return A list of the audio sources generated
     */
    public func genAudioSources(numberOfSources : Int) -> [AudioSource] {
        var sourceLst : [AudioSource] = [AudioSource]();
    
        for _ in 0 ..< numberOfSources {
            let audioSource : AudioSource! = genAudioSource();
            if audioSource != nil {
                sourceLst.append(audioSource)
            }
        }
        
        return sourceLst
    }
    
    /**
     * Create a vertex array object
     *
     * @return the identifier of the VAO created
     */
    private func createVAO() -> Int {
        var vaoID : GLuint = 0;
        glGenVertexArraysOES(1, &vaoID);
        vaos.append(vaoID);
        glBindVertexArrayOES(vaoID);
        return Int(vaoID);
    }
    
    
    /**
     * Store a certain element to be used in the program shader
     *
     * @param attributeNumber
     *            the id of the attribute to load in the program shader
     * @param coordinateSize
     *            Number of components of the attribute to store
     *
     * @param data
     *            Data to be store
     * @para dLength
     *            Number of elements that the data has
     */
    private func storeDataInAttributeList(attributeNumber : Int, coordinateSize : GLint, data :UnsafePointer<Void> , dLength : Int) {
        
        var vboID : GLuint = 0;
        
        glEnableVertexAttribArray(GLuint(attributeNumber));
        glGenBuffers(1, &vboID);
        self.vbos.append(vboID);
        
        // Bind the VBO just created
        glBindBuffer(GLenum(GL_ARRAY_BUFFER), vboID);
        let countBytes : Int = dLength * sizeof(CFloat);
        glBufferData(GLenum(GL_ARRAY_BUFFER), countBytes, data, GLenum(GL_STATIC_DRAW));
        glVertexAttribPointer(GLuint(attributeNumber), coordinateSize, GLenum(GL_FLOAT), RenderConstants.vertexNormalized, RenderConstants.STRIDE, BUFFER_OFFSET(0));
        // UnBind the current VBO
        glBindBuffer(GLenum(GL_ARRAY_BUFFER), 0);
        
    }
    
    /**
     * Load a shape in a VAO (Vertex array object)
     *
     * @param shape
     *            The shape to load
     *
     * @return A row model with information loaded
     */
    public func loadToVAO(shape : IShape) -> RawModel{
        
        let vaoID = self.createVAO();
        
        let indicesLength = shape.countIndices();
        let vertexLength = shape.countVertices();
        let textureLength = shape.countTextureCoords();
        let normalsLength = shape.countNormals();
        //Buffers
        let indicesData = shape.getIndices();
        let vertexData = shape.getVertices();
        let textureData = shape.getTextureCoords();
        let normalData = shape.getNormals();
        
        self.bindIndicesBuffer(indicesData, dLength: Int(indicesLength));
        
        self.storeDataInAttributeList(TEntityAttribute.position.rawValue, coordinateSize: RenderConstants.vertex, data: vertexData, dLength: Int(vertexLength))
        self.storeDataInAttributeList(TEntityAttribute.textureCoords.rawValue, coordinateSize: RenderConstants.texture, data: textureData, dLength: Int(textureLength))
        self.storeDataInAttributeList(TEntityAttribute.normal.rawValue, coordinateSize: RenderConstants.normal, data: normalData, dLength: Int(normalsLength))
        
        self.unbindVAO();
        return RawModel(vaoId: vaoID , indicesData: indicesData , indicesCount: Int(indicesLength));
        
    }
    
    /**
     *
     * @param externalMaterial
     *            A reference to an external material with all the information
     *            needed to create a material
     *
     * @return The material loaded
     */
    public func loadMaterial(shape : IShape) -> Material? {
        let externalMaterial : IExternalMaterial? = shape.getMaterial()
        
        
        if (externalMaterial == nil) {
            return nil;
        } else {
            var textureId : Int;
            var textureWeight : Float;
            var diffuseColor : ColorRGBA;
            
            if ((externalMaterial!.getDiffuseTextureFileName() == nil)
                || (externalMaterial!.getDiffuseTextureFileName() == "")) {
                textureId = 0;
                textureWeight = 0.0;
                diffuseColor = (externalMaterial!.getDiffuseColor() == nil) ? ColorRGBA(r: 0.0, g: 0.0, b: 0.0, a: 1.0) : ColorRGBA(color: externalMaterial!.getDiffuseColor()!);
            } else {
                textureId = self.loadTexture(externalMaterial!.getDiffuseTextureFileName()!);
                textureWeight = 1.0;
                diffuseColor = ColorRGBA(r: 0.0, g: 0.0, b: 0.0, a: 1.0);
            }
            let material : Material = Material(textureId);
            material.textureWeight = textureWeight
            material.diffuseColor = diffuseColor;
            
            return material;
        }
    }
    
    /**
     * Load a list of positions to VAO
     *
     * @param positions
     *            Positions to load
     * @para positionsLength
     *            Number of positions to load in the vertex array object
     * @param dimensions
     *            Number of components that the positions has
     *
     * @return The rawModel pointing to the created VAO
     */
    private func  loadPositionsToVAO(positions : UnsafePointer<Void>, positionsLength : Int, dimensions : Int) -> RawModel {
        
        let vaoID = self.createVAO();
        self.storeDataInAttributeList(TEntityAttribute.position.rawValue, coordinateSize: GLint(dimensions), data: positions, dLength: positionsLength);
        self.unbindVAO();
        
        return RawModel(vaoId: vaoID , indicesData: nil , indicesCount: (positionsLength/dimensions));
        
    }
    
    /**
     * Load a list of 2D positions to VAO
     *
     * @param positions
     *            Positions to load
     * @para positionsLength
     *            Number of positions to load in the vertex array object
     *
     * @return The rawModel pointing to the created VAO
     */
    public func load2DPositionsToVAO(positions : UnsafeMutablePointer<Float>, positionsLength : Int) -> RawModel{
        let dimensions = 2;
        return self.loadPositionsToVAO(positions, positionsLength: positionsLength, dimensions: dimensions);
    }
    
    /**
     * Load a list of 3D positions to VAO
     *
     * @param positions
     *            Positions to load
     * @para positionsLength
     *            Number of positions to load in the vertex array object
     *
     * @return The rawModel pointing to the created VAO
     */
    public func load3DPositionsToVAO(positions : UnsafeMutablePointer<Float>, positionsLength : Int) -> RawModel{
        let dimensions = 3;
        return self.loadPositionsToVAO(positions, positionsLength: positionsLength, dimensions: dimensions);
    }
    
    /**
     * When loads one texture defines that by default should zoom in/out it
     *
     * @param itarget
     *            the target of the filter
     */
    private func defineTextureFunctionFilters (itarget : Int32) {
        let target : GLenum = GLenum(itarget);
        
        glTexParameteri(target, GLenum(GL_TEXTURE_MIN_FILTER), GL_LINEAR);
        glTexParameteri(target, GLenum(GL_TEXTURE_MAG_FILTER), GL_LINEAR);
        glTexParameteri(target, GLenum(GL_TEXTURE_WRAP_S), GL_CLAMP_TO_EDGE);
        glTexParameteri(target, GLenum(GL_TEXTURE_WRAP_T), GL_CLAMP_TO_EDGE);
        
    }
    
    /**
     *  Load one texture from a file and set it in openGL
     *
     * @param fileName
     *            Name of the file to load without the .png extension in the end
     *
     * @return Identifier of the texture loaded
     */
    public func loadTexture(fileName : String) -> Int {
        var imagePath : String! = NSBundle.mainBundle().pathForResource(fileName, ofType: PNG_EXTENSION)
        
        if(imagePath == nil) {
            let fName = fileName.componentsSeparatedByString(".")
            imagePath = NSBundle.mainBundle().pathForResource(fName[0], ofType: PNG_EXTENSION)
        }
        
        if(imagePath == nil) {
            print("Impossible to get the patch to \(fileName)");
            return -1;
        }
        let textureData : TextureData! = LoadUtils.loadTexture(imagePath);
        
        if (textureData == nil) {
            print("Impossible to get the image \(imagePath)");
            return -1;
        } else {
            var textureId : GLuint = 0;
            glGenTextures(1, &textureId);
            glBindTexture(GLenum(GL_TEXTURE_2D), textureId);
            
            let width : GLsizei = GLsizei(textureData.width)
            let height : GLsizei = GLsizei(textureData.height)
            let buffer : UnsafeMutablePointer<Void> = textureData.buffer
            
            
            glTexImage2D(GLenum(GL_TEXTURE_2D), 0, GL_RGBA,  width, height, 0,
                         GLenum(GL_RGBA), GLenum(GL_UNSIGNED_BYTE),  buffer);
            
            self.defineTextureFunctionFilters(GL_TEXTURE_2D);
            self.textures.append(textureId);
            return Int(textureId);
            
        }
    }
    
    
    /**
     *  Loads the data of a texture without bind
     *
     * @param fileName
     *            Name of the file to load without the .png extension in the end
     *
     * @return The texture read from the file without any openGL bind
     */
    public func getTextureData(fileName : String) -> TextureData! {
        let imagePath : String! = NSBundle.mainBundle().pathForResource(fileName, ofType: PNG_EXTENSION)
        
        if(imagePath == nil) {
            print("Impossible to get the patch to \(fileName)");
            return nil;
        } else {
            let textureData : TextureData! = LoadUtils.loadTexture(imagePath);
            return textureData;
        }
    }
    
    /**
     * Loads a cubic texture
     *
     * @param fileNames
     *            Names of the file to load without the .png extension in the
     *            end
     *
     * @return Identifier of the texture cubic texture loaded
     */
    public func loadTCubeMap(fileNames : Array<String>!) -> Int {
        if (fileNames == nil) {
            return -1;
        } else {
            let cubicTextureTargets : Array<Int32> = [GL_TEXTURE_CUBE_MAP_POSITIVE_X,
                                                      GL_TEXTURE_CUBE_MAP_NEGATIVE_X, GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
                                                      GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
                                                      GL_TEXTURE_CUBE_MAP_NEGATIVE_Z ];
            
            var textureId : GLuint = 0;
            glGenTextures(1, &textureId);
            glActiveTexture(GLenum(GL_TEXTURE0));
            glBindTexture(GLenum(GL_TEXTURE_CUBE_MAP), textureId);
            
            for i in 0 ..< NUMBER_CUBE_FACES {
                let fileName = fileNames[i];
                
                let imagePath : String! = NSBundle.mainBundle().pathForResource(fileName, ofType: PNG_EXTENSION)
                let textureData = LoadUtils.loadTexture(imagePath);
                
                if (textureData == nil) {
                    return -1;
                } else {
                    let target = cubicTextureTargets[i];
                    let width : GLsizei = GLsizei(textureData.width)
                    let height : GLsizei = GLsizei(textureData.height)
                    let buffer : UnsafeMutablePointer<Void> = textureData.buffer
                    
                    glTexImage2D(GLenum(target), 0, GL_RGBA,  width, height, 0,
                                 GLenum(GL_RGBA), GLenum(GL_UNSIGNED_BYTE),  buffer);
                }
            }
            self.defineTextureFunctionFilters(GL_TEXTURE_CUBE_MAP);
            self.textures.append(textureId);
            return Int(textureId);
            
        }
    }
    
    
    private func MyGetOpenALAudioData(inFileURL: NSURL, inout _ outDataSize: ALsizei, inout _ outDataFormat: ALenum, inout _ outSampleRate: ALsizei) -> UnsafeMutablePointer<Void> {
        var err: OSStatus = noErr
        var theFileLengthInFrames: Int64 = 0
        var theFileFormat: AudioStreamBasicDescription = AudioStreamBasicDescription()
        var thePropertySize: UInt32 = UInt32(strideofValue(theFileFormat))
        var extRef: ExtAudioFileRef = nil
        var theData: UnsafeMutablePointer<CChar> = nil
        var theOutputFormat: AudioStreamBasicDescription = AudioStreamBasicDescription()
        
        Exit: do {
            // Open a file with ExtAudioFileOpen()
            err = ExtAudioFileOpenURL(inFileURL, &extRef)
            if err != 0 { print("MyGetOpenALAudioData: ExtAudioFileOpenURL FAILED, Error = \(err)"); break Exit }
            
            // Get the audio data format
            err = ExtAudioFileGetProperty(extRef, kExtAudioFileProperty_FileDataFormat, &thePropertySize, &theFileFormat)
            if err != 0 { print("MyGetOpenALAudioData: ExtAudioFileGetProperty(kExtAudioFileProperty_FileDataFormat) FAILED, Error = \(err)"); break Exit }
            if theFileFormat.mChannelsPerFrame > 2 { print("MyGetOpenALAudioData - Unsupported Format, channel count is greater than stereo"); break Exit }
            
            // Set the client format to 16 bit signed integer (native-endian) data
            // Maintain the channel count and sample rate of the original source format
            theOutputFormat.mSampleRate = theFileFormat.mSampleRate
            theOutputFormat.mChannelsPerFrame = theFileFormat.mChannelsPerFrame
            
            theOutputFormat.mFormatID = kAudioFormatLinearPCM
            theOutputFormat.mBytesPerPacket = 2 * theOutputFormat.mChannelsPerFrame
            theOutputFormat.mFramesPerPacket = 1
            theOutputFormat.mBytesPerFrame = 2 * theOutputFormat.mChannelsPerFrame
            theOutputFormat.mBitsPerChannel = 16
            theOutputFormat.mFormatFlags = kAudioFormatFlagsNativeEndian | kAudioFormatFlagIsPacked | kAudioFormatFlagIsSignedInteger
            
            // Set the desired client (output) data format
            err = ExtAudioFileSetProperty(extRef, kExtAudioFileProperty_ClientDataFormat, UInt32(strideofValue(theOutputFormat)), &theOutputFormat)
            if err != 0 { print("MyGetOpenALAudioData: ExtAudioFileSetProperty(kExtAudioFileProperty_ClientDataFormat) FAILED, Error = \(err)"); break Exit }
            
            // Get the total frame count
            thePropertySize = UInt32(strideofValue(theFileLengthInFrames))
            err = ExtAudioFileGetProperty(extRef, kExtAudioFileProperty_FileLengthFrames, &thePropertySize, &theFileLengthInFrames)
            if err != 0 { print("MyGetOpenALAudioData: ExtAudioFileGetProperty(kExtAudioFileProperty_FileLengthFrames) FAILED, Error = \(err)"); break Exit }
            
            // Read all the data into memory
            let dataSize = UInt32(theFileLengthInFrames) * theOutputFormat.mBytesPerFrame
            theData = UnsafeMutablePointer.alloc(Int(dataSize))
            if theData != nil {
                var theDataBuffer: AudioBufferList = AudioBufferList()
                theDataBuffer.mNumberBuffers = 1
                theDataBuffer.mBuffers.mDataByteSize = dataSize
                theDataBuffer.mBuffers.mNumberChannels = theOutputFormat.mChannelsPerFrame
                theDataBuffer.mBuffers.mData = UnsafeMutablePointer(theData)
                
                // Read the data into an AudioBufferList
                var ioNumberFrames: UInt32 = UInt32(theFileLengthInFrames)
                err = ExtAudioFileRead(extRef, &ioNumberFrames, &theDataBuffer)
                if err == noErr {
                    // success
                    outDataSize = ALsizei(dataSize)
                    outDataFormat = (theOutputFormat.mChannelsPerFrame > 1) ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16
                    outSampleRate = ALsizei(theOutputFormat.mSampleRate)
                } else {
                    // failure
                    theData.dealloc(Int(dataSize))
                    theData = nil // make sure to return NULL
                    print("MyGetOpenALAudioData: ExtAudioFileRead FAILED, Error = \(err)"); break Exit;
                }
            }
        }
        
        // Dispose the ExtAudioFileRef, it is no longer needed
        if extRef != nil { ExtAudioFileDispose(extRef) }
        return UnsafeMutablePointer(theData)
    }
    
    private func MyFreeOpenALAudioData(data: UnsafeMutablePointer<Void>, _ dataSize: ALsizei) {
        let theData = UnsafeMutablePointer<CChar>(data)
        if theData != nil {
            theData.dealloc(Int(dataSize))
        }
    }
    
    /**
     * Load one audio file in a buffer
     *
     * @param fileName
     *            The file name of the file to load
     *
     * @return The identifier of the buffer return by open GL
     */
    public func loadSound(fileName : String) -> AudioBuffer! {
        var audioBuffer : AudioBuffer! = nil;
        
        var bufferId : ALuint = 0
        
        alGenBuffers(1, &bufferId)
        
        
        
        /**
         * Was not possible to one buffer return AL false
         */
        if (alGetError() != AL_NO_ERROR) {
            return nil;
        }
        
        let filePath : String! = NSBundle.mainBundle().pathForResource(fileName, ofType: "aac")
        if(filePath == nil) {
            print("Impossible to get the patch to audio \(fileName) ");
            return nil;
        }

        var format: ALenum = 0
        var size: ALsizei = 0
        var freq: ALsizei = 0
        
        let fileURL : NSURL = NSURL(fileURLWithPath: filePath)
        
        var data: UnsafeMutablePointer<Void> = nil
        data = MyGetOpenALAudioData(fileURL, &size, &format, &freq)

        
        alBufferData(bufferId, format, data, size, freq)
        
        audioBuffer = AudioBuffer(ALint(bufferId));
        
        MyFreeOpenALAudioData(data, size)
        return audioBuffer;

        
        
        
        /*
         FileInputStream fin = null;
         BufferedInputStream bin = null;
         OggData oggFile = null;
         try {
         fin = new FileInputStream(RESOURCES_FOLDER + fileName + OGG_EXTENSION);
         bin = new BufferedInputStream(fin);
         oggFile = oggDecoder.getData(bin);
         
         if (oggFile == null) {
         // Was not possible read the file so releases the resources
         AL10.alDeleteBuffers(bufferId);
         } else {
         audioBuffers.add(bufferId);
         int oggFormat = oggFile.channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16;
         AL10.alBufferData(bufferId, oggFormat, oggFile.data, oggFile.rate);
         // oggFile.dispose();
         audioBuffer = new AudioBuffer(bufferId);
         }
         } catch (Exception e) {
         e.printStackTrace();
         } finally {
         if (oggFile != null) {
         oggFile.data.clear();
         oggFile = null;
         }
         try {
         if (bin != null) {
         bin.close();
         }
         if (fin != null) {
         fin.close();
         }
         } catch (IOException e) {
         e.printStackTrace();
         }
         }*/
    }
    
    /**
     * UnBind the current vertex array object
     */
    private func unbindVAO() {
        glBindVertexArrayOES(0);
    }
    
    /**
     *
     * @param indices
     *            the indices to vertex buffer object
     * @para dLength
     *            Number of indices that the data has
     */
    private func bindIndicesBuffer(indices : UnsafePointer<Void>, dLength : Int) {
        
        
        var vboID : GLuint = 0;
        glGenBuffers(1, &vboID);
        vbos.append(vboID);
        
        // Bind the VBO just created
        glBindBuffer(GLenum(GL_ARRAY_BUFFER), vboID)
        let countBytes = dLength * sizeof(CUnsignedShort);
        glBufferData(GLenum(GL_ARRAY_BUFFER), countBytes, indices, GLenum(GL_STATIC_DRAW))
        
    }
    
    /**
     Desallocate memory
     */
    deinit {
        
        vaos = nil;
        vbos = nil;
        textures = nil;
    }
    
}
