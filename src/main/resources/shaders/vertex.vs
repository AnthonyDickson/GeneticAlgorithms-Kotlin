#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoordinates;

out vec2 outTextureCoordinates;

uniform mat4 viewModel;
uniform mat4 projection;

void main()
{
    gl_Position = projection * viewModel * vec4(position, 1.0);
    outTextureCoordinates = textureCoordinates;
}