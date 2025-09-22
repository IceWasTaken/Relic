#version 430 core

uniform vec3 eyePos;
uniform vec3 ray0;
uniform vec3 ray1;
uniform vec3 ray2;
uniform vec3 ray3;

uniform float time;
uniform float blendFactor;

#define PI 3.14
#define TWO_PI 6.28

struct Ray {
    vec3 orgin;
    float direction;
};

struct IntersectInfo {
    float t;
    vec3 p;
    vec3 normal;


};

void main(void) {
    vec3 finalColor = mix();
}