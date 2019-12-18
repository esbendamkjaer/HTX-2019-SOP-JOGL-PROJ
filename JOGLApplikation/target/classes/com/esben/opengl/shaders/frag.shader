#version 430

out vec4 test;
out vec4 color;

uniform mat4 mvpMatrix;

void main(void) {
	test = vec4(1.0, 0.0, 0.0, 1.0);
}

