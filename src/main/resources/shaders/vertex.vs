#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoordinates;
layout (location=2) in vec3 normal;

out vec2 outTextureCoordinates;
out vec3 outNormal;
out vec3 outPosition;

uniform mat4 viewModel;
uniform mat4 projection;

void main()
{
    vec4 worldPosition = viewModel * vec4(position, 1.0);
    gl_Position = projection * worldPosition;

    outTextureCoordinates = textureCoordinates;
    outNormal = normalize(viewModel * vec4(normal, 0.0)).xyz;
    outPosition = worldPosition.xyz;
}