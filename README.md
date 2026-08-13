# Relic

Java based game engine. Features distinct backends for OpenGL and Vulkan.

## Description

Heavily in development game engine. Based upon bindings from LWJGL. 

## Dependencies

- LWJGL 3.4.0
   - Includes bindings for:
     - Assimp
     - GLFW
     - OpenGL
     - OpenAL
     - Shaderc
     - STB
     - Vulkan
     - OpenXR
- OpenGL
    - OpenGL 4.6 Capable GPU
      - Support for GL_ARB_bindless_texture
      - Support for GL_ARB_gpu_shader_int64
      - Support for either GL_NVX_gpu_memory_info or GL_ATI_meminfo not required but HEAVILY recommended
- Vulkan
    - Vulkan 1.3 Capable GPU
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

* 0.4
  * Beginning steps on Curio package
    * Moved most GLFW specific methods and objects to said package
  * Added console system to testing application, which would eventually be merged into Relic's core
  * Deleted or moved files/classes that were no longer used or were replaced
  * Move general use, non-relic specific classes and utils to own package, Heirloom

* 0.3
  * When modern day Relic really started to take hold
  * Refactor that created the RelicApplication class and better versioning
  * Introduced a now scrapped Vulkan implementation
* 0.2
  * Also really tracked on GitHub
  * Introduced bindless textures
* 0.1
  * Mostly just a collection of other people's code. 
  * Wasn't tracked on GitHub, so I have no idea what it contained

  
