/*Variable with position of the entity*/
attribute vec3 position;

/*Variable with coordinates of the textures of the entity*/
attribute vec2 textureCoords;

/*Variable with normals of the entity*/
attribute vec3 normal;


/*Outputs because we are sending them to the fragment shader*/
/*The coordinates of the texture as output*/
varying lowp vec2 pass_textureCoords;


/*The vector normal to the surface as output*/
varying lowp vec3 surfaceNormal;

/*The vector that indicates where the light is in relation to the object*/
varying lowp vec3 toLightVector;

/* vertex from the vertex to the camera*/
varying lowp vec3 toCameraVector;

/*The visibility of the vertice in order to simulate fog*/
varying lowp float visibility;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

/*Position where the light of the scene is*/
uniform vec3 lightPosition;

/*Density of fog*/
const float fog_density = 0.007;

/*Gradient of fog*/
const float fog_gradient = 1.5;

void main()
{
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    
    /*Position of the element relative to the camera*/
    vec4 positionRelativeToCam = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRelativeToCam;
    pass_textureCoords = textureCoords;
    
    
    surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
    toLightVector = lightPosition - worldPosition.xyz;
    
    /*used for the specular light, first get the position of the camera. second subtract the position of the vertex */
    //TODO was an inverse used here
    toCameraVector = (viewMatrix * vec4(0.0,0.0,0.0, 1.0)).xyz - worldPosition.xyz;
    
    /*Distance from the vertice to the camera*/
    float distance = length(positionRelativeToCam.xyz);
    
    /*Compute visibility of the vertice*/
    visibility = exp(-pow((distance * fog_density), fog_gradient));
    visibility = clamp(visibility, 0.0, 1.0);
    
}
