#version 330

in  vec2 outTextureCoordinates;
out vec4 fragmentColour;

uniform sampler2D textureSampler;
uniform vec3 colour;
uniform int useColour;

void main()
{
    if (useColour == 1) {
        fragmentColour = vec4(colour, 1.0);
    } else {
        fragmentColour = texture(textureSampler, outTextureCoordinates);
    }
}