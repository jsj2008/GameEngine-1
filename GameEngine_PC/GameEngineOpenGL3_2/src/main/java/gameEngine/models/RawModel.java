package gameEngine.models;

/**
 * Represents one raw model of one entity
 */
public class RawModel {

	/**
	 * Identifier of the vertex array object of the raw model
	 */
	private final int vaoId;

	/**
	 * Number of vertices of the raw model
	 */
	private final int vertexCount;

	/**
	 * Constructor of the raw model
	 * 
	 * @param vaoId
	 *            The identifier of vertex array object assigned by openGL
	 * @param vertexCount
	 *            number of vertex
	 */
	public RawModel(int vaoId, int vertexCount) {
		super();
		this.vaoId = vaoId;
		this.vertexCount = vertexCount;
	}

	/**
	 * @return the The identifier of vertex array object assigned by openGL
	 */
	public int getVaoId() {
		return vaoId;
	}

	/**
	 * @return the number of vertex
	 */
	public int getVertexCount() {
		return vertexCount;
	}

}
