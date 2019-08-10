#version 330


//====================//
// Struct Definitions //
//====================//
struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation attenuation;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float reflectance;
    int hasTexture;
};

//===========//
// Variables //
//===========//
in  vec2 outTextureCoordinates;
in  vec3 outNormal;
in  vec3 outPosition;
out vec4 fragmentColour;

uniform Material material;
uniform sampler2D textureSampler;

uniform vec3 ambientLight;
uniform float specularPower;
uniform PointLight pointLight;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

//======================//
// Function Definitions //
//======================//

void setupColours(Material material, vec2 textureCoordinates) {
    if (material.hasTexture == 1) {
        ambientC = texture(textureSampler, textureCoordinates);
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calculatePointLight(PointLight light, vec3 position, vec3 normal)
{
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specularColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    vec3 lightDirection = light.position - position;
    vec3 toLightSource  = normalize(lightDirection);
    float diffuseFactor = max(dot(normal, toLightSource ), 0.0);
    diffuseColour = diffuseC * vec4(light.colour, 1.0) * light.intensity * 
        diffuseFactor;

    // Specular Light
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightSource = -toLightSource;
    vec3 reflectedLight = normalize(reflect(fromLightSource, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColour = specularC * specularFactor * material.reflectance * 
        vec4(light.colour, 1.0);

    // Attenuation
    float distance = length(lightDirection);
    float attenuationInverse = light.attenuation.constant + 
        light.attenuation.linear * distance +
        light.attenuation.exponent * distance * distance;
        
    return (diffuseColour + specularColour) / attenuationInverse;
}

void main() {
    setupColours(material, outTextureCoordinates);
    
    vec4 diffuseSpecularComponent = calculatePointLight(pointLight, outPosition,
                                                        outNormal);

    fragmentColour = ambientC * vec4(ambientLight, 1.0) + diffuseSpecularComponent;
}