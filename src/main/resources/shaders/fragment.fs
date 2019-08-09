#version 330

in  vec2 outTextureCoordinates;
out vec4 fragmentColour;

uniform sampler2D textureSampler;

void main()
{
    fragmentColour = texture(textureSampler, outTextureCoordinates);
}