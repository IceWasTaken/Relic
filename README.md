# Relic

Java based game engine. Features distinct backends for OpenGL and Vulkan.

## Description

Heavily in development game engine. Based upon bindings from LWJGL. 
Contains 3 main modules:
* Relic
  * The engine itself. Depends on Curio and Heirloom
* Curio
  * Backend that wraps OpenGL, Vulkan, GLFW, etc into one public facing API that Relic uses
* Heirloom
  * Non-relic specific utilities, basically the only package that makes sense for use outside of this project

## Dependencies
### Relic
  - Curio
  - Heirloom
  - LWJGL
    - GLFW
    - OpenGL
    - Vulkan
    - Shaderc
    - Assimp
  - ImGui (SpaiR)
  - Tinylog
  - Classgraph
  - JOML
  

### Curio 
  - Heirloom
  - LWJGL
    - GLFW
    - OpenGL
    - Vulkan
    - STB
    - VMA
  - ImGui (SpaiR)
  - Tinylog
  - JOML


### Heirloom
  - Classgraph
  - JOML
  - Tinylog

### OpenGL 4.6
- Support for GL_ARB_bindless_texture
- Support for GL_ARB_gpu_shader_int64
- Support for either GL_NVX_gpu_memory_info or GL_ATI_meminfo (optional)
### Vulkan 1.3+
- Support for VK_KHR_swapchain

## Getting Started

### Dependencies
* See Dependencies above

### Installing
* Import Gradle project into your IDE of choice. 
* May require you to specify which GPU to use depending on your OS's configuration.


## Authors
Just me. icewastaken_ on discord. 

## Version History

* 0.6.1
  * Separated Relic, Heirloom, Curio into different modules
* 0.6
  * Implemented asset streaming
  * Material/Map/Instance buffers separated into different classes
  * OpenGL
    * Now using DSA calls
    * Buffers now mapped (glMapNamedBufferRange)
* 0.5
  * Beginning steps on Curio package
    * Moved most GLFW specific methods and objects to said package
  * Added console system to testing application, which would eventually be merged into Relic's core
  * Deleted or moved files/classes that were no longer used or were replaced
  * Move general use, non-relic specific classes and utils to own package, Heirloom
* 0.1 - 0.4
  * Not/barely tracked

  
