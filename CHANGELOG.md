# Changelog
### Thought the version history in README.md was getting too long. Any update after 0.6.1 will be found here.

## 0.6.4
> Basic particle system & rendering (October 8th, 2026)

### Relic
* Created GLParticleRenderer.java - What it sounds like. Renders particles.
* Created GLSwapRenderer.java - Swaps framebuffer to main framebuffer
* Created Particle.java - Holds a particle (is anyone ever going to read this shit?)
* Created ParticleSystem.java - Handler for lifecycle of a particle object
* Created PhysicsUtil.java - Utility for physics related methods (i mean, really, does anyone need these comments to know what this class is for?)
* Modified BufferManager.java - Now checks to see if instances/materials have changed before re-buffering data
* Modified GLLightRenderer.java - Now uses GLPipeline system, draws into its own framebuffer now
* Modified Relic.java - Bump version

### Curio
* Modified GLTexture.java - Now checks if RenderDoc is installed before generating bindless handles
* Modified GLFramebuffer.java - Added wrapped method for glBlitNamedFramebuffer, added GraphicsContext field
* Modified GLPipeline.java - Added new constructor with default params, now checks shader status and throws error if not correct
* Modified Window.java - New abstract method for getting window scale
* Modified WindowAttribute.java - Added enums for framebuffer Stencil & Depth bit count

### Other
* Created particle.vert & particle.frag
---

## 0.6.3
> Change to OpenGL core 4.2; Create new NIO buffer types for Vec2i/2f/3f (October 6th, 2026)

### Relic
* Created GLAARenderer.java
* Modified GLRenderer.java - Now uses Renderer superclass from Curio instead of now deleted class from Relic
* Modified GLGuiRenderer.java - Now correctly scales GUI and mouse collisions based on framebuffer scale
* Modified GLLightRenderer.java - Now uses GLPipeline for rendering state information
* Modified GLSceneRenderer.java - Now uses alternative GLPipeline constructor
* Modified InstanceBuffer.java - Now holds buffers that used to be defined in MaterialMapBuffer
* Modified MaterialCache.java - Removed default material
* Modified Relic.java - Bump version to 0.6.3
* Modified RelicApplication.java - Separates window resizing and framebuffer resizing into two different checks
* Now uses OpenGL Extension calls instead of core 4.2+ calls:
  * Buffer.java
  * GLAnimationRenderer.java
  * GLCommandBuffer.java
  * GLShader.java
  * GuiMesh.java
  * MeshBuffer.java
  * SceneInfoBuffer.java
  * ShadowBuffer.java
  * SwapBuffer.java
* No longer uses deleted Lifecycle.java:
  * BufferManager.java
  * GLGuiRenderer.java
  * GLLightRenderer.java
  * GLManager.java (deprecated)
  * GLPostRenderer.java
  * GLSceneRenderer.java
  * GLShadowRenderer.java
  * InstanceBuffer.java
  * MeshBuffer.java
  * Scene.java
  * SceneInfoBuffer.java
  * VulkanSceneRenderer.java
  * VulkanRenderer.java
* Deleted MaterialMapBuffer.java
* Deleted IShader.java
* Deleted IShaderProgram.java
* Deleted Renderer.java
  
### Curio
#### OpenGL
* Created GLSamplers.java - Modified version of GLSampler.java, providing easy access to sampler objects created once
* Modified GLPipeline.java - Added constructor with default parameters
* Modified OpenGLContext.java:
  * Now requires following extensions:
    * GL_ARB_texture_filter_anisotropic
    * GL_ARB_direct_state_access
    * GL_ARB_clip_control
    * GL_ARB_buffer_storage
    * GL_ARB_compute_shader
    * GL_ARB_shader_storage_buffer_object
    * GL_ARB_framebuffer_no_attachments
    * GL_ARB_multi_draw_indirect
  * Added field holding GLSamplers & and associated getter
  * Uses SAMPLER_TRILINEAR_FILTERING by default for textures
* Now uses OpenGL Extension calls instead of core 4.2+ calls:
  * GLShader.java
  * GLShader.java (not a typo, just so much fucking technical debt)
  * FramebufferObject.java
  * GLBuffer.java
  * GLFramebuffer.java
  * GLShader.java
  * GLViewport.java
  * SystemInfo.java
  * VertexArrayObject.java
* Deleted GLGPUBuffer.java
* Deleted GLSampler.java - *See GLSamplers.java
#### Vulkan
* Modified Instance.java - Now uses instance extensions based on the window backend
* No longer uses deleted Lifecycle.java:
  * Device.java
  * Instance.java
  * Surface.java
* Deleted VulkanWindow.java
#### GLFW
* Created GLFW.java - Mimics LWJGL's GLFW.class, but uses single Vector2iBuffer instead of dual IntBuffer(s)
* Modified GLFWWindow.java:
  * Replaced width/height fields with a Vector2i
  * New framebufferSize, supplied by glfwGetFramebufferSize
  * New windowResized field & getter
  * New framebufferResized field & getter
#### STB
* Created SDL.java - Mimics LWJGL's SDL.class, but uses single Vector2iBuffer instead of dual IntBuffer(s)
* Modified SDLWindow.java:
  * Replaced width/height fields with a Vector2i
  * New framebufferSize, supplied by SDL_GetWindowSizeInPixels
  * New windowResized field & getter
  * New framebufferResized field & getter
* No longer uses deleted Lifecycle.java:
  * Bitmap.java
#### General
* Created Vector2iBuffer.java - ByteBuffer wrapper that holds a JOML Vector2i
* Created Vector2fBuffer.java - ByteBuffer wrapper that holds a JOML Vector2f
* Created Vector3fBuffer.java - ByteBuffer wrapper that holds a JOML Vector3f
* Created Renderer.java - Abstract class for renderers & their lifecycle
* Created Buffer.java - Buffer for storing objects of type T inside a wrapped ByteBuffer
* Modified DepthState.java - Added default DepthState static object
* Modified RasterizationState.java - Added default RasterizationState static object
* Modified RendererConfig.java - Removed legacy config variables
* Deleted Renderer.java (not the same as above)
* No longer uses deleted Lifecycle.java:
  * Fence.java
  * GraphicsContext.java
  * Image.java
  * Texture.java
  * Viewport.java

### Heirloom
* Modify RegistrationManager.java - Adds method for closing open registrations
* Deleted Lifecycle.java
* Deleted BufferUtil.java

---

## 0.6.2 
> Added SDL support. Made keyboard/mouse input agnostic to window API. (September 22, 2026)

### Relic
* Created EntityEvent.java - Includes subclasses for entity lifecycle
* Modified BufferManager.java - Added additional logging (loading times)
* Modified Entity.java - Now triggers events when certain actions are triggered
* Modified GLFramebuffer.java - Recreates FBO on resize
* Modified GLPipeline.java - Forces GL_FRONT_AND_BACK with glPolygonMode (Only valid operation on GL 3.1+)
* Modified RelicApplication.java - Now extends Application abstract class from Heirloom
* Moved ApplicationProperties.java to Heirloom library
* Deleted ApplicationContext.java
* Deleted DummyScene.java

### Curio
#### OpenGL
* Created GLSampler.java - Wrapper for OpenGL sampler object
* Modified GLTexture.java - Forces uses of GLSampler in creation
* Modified OpenGLContext.java - Now includes method for setting OpenGL specific window attributes. Now uses trilinear sampling for textures by default
  

#### Vulkan
* Created PipelineCache.java - Wrapper for Vulkan pipeline cache
* Created VkPipeline.java - Wrapper for Vulkan pipeline object
* Modified ImageView.java - Added method for image barrier
* Modified Attachment.java
  

#### GLFW
* Created GLFWKey.java - Utility class for mapping GLFW key enums to Key.java enums
* Modified GLFWWindow.java - Adds more callback fields, now extends Window.java
  

#### SDL (New!)
* Created Action.java - Describes how a mouse/keyboard key is pressed
* Created SDLKey.java - Utility class for mapping SDL key enums to Key.java enums
* Created SDLWindow.java - Class for handling and managing an SDL window
  

#### General
* Created MouseButtonEvent.java - Window API agnostic event for handling mouse button events
* Created Key.java - Enum class containing standard keyboard keys (based on existing GLFW enums, but is agnostic)
* Created MouseButton.java - Enum class containing standard mouse keys (based on existing GLFW enums, but is agnostic)
* Created ShaderCompilier.java - Utility for compiling GLSL to SPIR-V
* Modified Window.java - Now agnostic to a specific API; it now holds abstract methods for backends to implement on their own
* Modified Input.java - Now agnostic to a specific API, now accepting generic Key.java enums
* Modified ImageFormat.java - Added DEPTH_16_UNSIGNED_NORMALIZED enum
* Modified Curio.java - Now accepts any generic Application.java (heirloom) object instead of specifically RelicApplication.java
* Modified GLFWConfig.java - Deprecated in favor of a render agnostic config, now uses Wayland by default for Linux
* Modified - Now Window API agnostic:
    * CursorEnterEvent.java
    * CursorEvent.java
    * KeyEvent.java
* Modified build.gradle - Added dependencies for shaderc and SDL
* Moved to window API agnostic package:
    * CursorEnterEvent.java
    * CursorEvent.java
    * KeyEvent.java


### Heirloom
* Create Functions.java - Class holding functional interfaces with varying argument counts
* Create Application.java - Generic abstract class for the lifecycle of an application
* Modify RegistrationManger.java - Adds method for closing registries
* Modify Version.java - Adds JavaDoc comments
