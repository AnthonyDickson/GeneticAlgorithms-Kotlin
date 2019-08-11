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

struct SpotLight {
    PointLight pointLight;
    vec3 direction;
    float cosineConeAngle;
};

struct DirectionalLight {
    vec3 colour;
    vec3 direction;
    float intensity;
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
uniform SpotLight spotLight;
uniform DirectionalLight directionalLight;

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

vec4 calculateLightColour(vec3 lightColour, vec3 toLightDirection, float lightIntensity, vec3 position, vec3 normal) {
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specularColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, toLightDirection ), 0.0);
    diffuseColour = diffuseC * vec4(lightColour, 1.0) * lightIntensity * diffuseFactor;

    // Specular Light
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDirection = -toLightDirection;
    vec3 reflectedLight = normalize(reflect(fromLightDirection, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColour = specularC * specularFactor * material.reflectance * vec4(lightColour, 1.0);

    return diffuseColour + specularColour;
}

vec4 calculateDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calculateLightColour(light.colour, normalize(light.direction), light.intensity, position, normal);
}

vec4 calculatePointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection  = normalize(lightDirection);
    vec4 diffuseSpecular = calculateLightColour(light.colour, toLightDirection, light.intensity, position, normal);

    // Attenuation
    float distance = length(lightDirection);
    float attenuationInverse = light.attenuation.constant + 
        light.attenuation.linear * distance +
        light.attenuation.exponent * distance * distance;
        
    return diffuseSpecular / attenuationInverse;
}

vec4 calculateSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.pointLight.position - position;
    vec3 toLightDirection  = normalize(lightDirection);
    float alpha = dot(normalize(light.direction), -toLightDirection);

    vec4 colour = vec4(0, 0, 0, 0);

    if (alpha > light.cosineConeAngle) {
        colour = calculatePointLight(light.pointLight, position, normal);
        colour *= (1.0 - (1.0 - alpha)/(1.0 - light.cosineConeAngle));
    }

    return colour;
}

void main() {
    setupColours(material, outTextureCoordinates);
    
    vec4 diffuseSpecularComponent = calculateDirectionalLight(directionalLight, outPosition, outNormal);
    diffuseSpecularComponent += calculatePointLight(pointLight, outPosition, outNormal);
    diffuseSpecularComponent += calculateSpotLight(spotLight, outPosition, outNormal);

    fragmentColour = ambientC * vec4(ambientLight, 1.0) + diffuseSpecularComponent;
}