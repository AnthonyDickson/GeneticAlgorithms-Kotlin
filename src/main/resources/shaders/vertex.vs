#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec3 colour;

out vec3 vertexColour;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    gl_Position = viewMatrix * projectionMatrix * vec4(position, 1.0);
    vertexColour = colour;
}