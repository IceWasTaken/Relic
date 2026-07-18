package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.curio.library.opengl.object.buffer.DrawIndirectBuffer;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.Shadows;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLManager;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShader;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.depricated.buffer.ShadowBuffer;
import net.ice.relic.core.rendering.backend.opengl.depricated.buffer.UniformBufferObject;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Model;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.scene.SceneObject;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_LOWER_LEFT;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;
import static org.lwjgl.opengl.GL45.*;

public class GLShadowRenderer implements Lifecycle {


    private GLShaderProgram shaderProgram;
    private final GLRenderer glRenderer;

    private final Viewport viewport;

    private int staticDrawCount;
    private DrawIndirectBuffer staticCommandBuffer;

    private Shadows shadows;

    private UniformBufferObject uniforms;

    private Map<String, Integer> objectIndexMap;

    public GLShadowRenderer(GLRenderer glRenderer) {
        this.glRenderer = glRenderer;
        this.viewport = glRenderer.getApplication().getCurio().getGraphicsContext().createViewport(4096, 4096);
        this.objectIndexMap = new HashMap<>();
        this.shadows =  new Shadows();
    }

    @Override
    public void init() {
        this.shaderProgram = new GLShaderProgram().attach(List.of(
                new GLShader(ShaderType.VERTEX).load("shadow.vert", ShaderType.VERTEX, false),
                new GLShader(ShaderType.GEOMETRY).load("shadow.geom", ShaderType.GEOMETRY, false),
                new GLShader(ShaderType.FRAGMENT).load("shadow.frag", ShaderType.FRAGMENT, false)
        ));
        this.uniforms = new UniformBufferObject(shaderProgram);
        for (int i = 0; i < 200; i++) {
            uniforms.createUniform("instances[" + i + "]" + ".modelMatrix");
            uniforms.createUniform("instances[" + i + "]" + ".materialIndex");
        }
        for (int i = 0; i < 3; i++) {
            uniforms.createUniform("projViewMatrices[" + i + "]");
        }


    }

    @Override
    public void render() {
        shaderProgram.bind();
        shadows.update(glRenderer.getApplication().getCurrentScene());
        glRenderer.getShadowBuffer().bindFramebuffer();
        glClipControl(GL_LOWER_LEFT, GL_NEGATIVE_ONE_TO_ONE);
        glDepthRange(-1, 1);
        glDepthFunc(GL_LESS);
        glClearDepthf(1.0f);
        glViewport(0, 0, 4096, 4096);

        glClearColor(1.f, 1.f, 0f, 0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        int drawElement = 0;
        for (Model model : glRenderer.getApplication().getCurrentScene().getModels().values()) {
            if (model.isAnimated()) continue;
            for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                for (SceneObject object : model.getSceneObjects()) {
                    String name = uniforms.formatUniform("instances", drawElement);
                    uniforms.setUniform(name + ".modelMatrix", object.getTransform().getTransformMatrix());
                    uniforms.setUniform(name + ".materialIndex", meshDrawData.materialIdx());
                    drawElement++;
                }
            }
        }
        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            Matrix4f shadowData = shadows.getShadowData().get(i).getProjViewMatrix();
            uniforms.setUniform("projViewMatrices[" + i + "]", shadowData);
        }

        staticCommandBuffer.bind();
        glRenderer.getStaticArrayObject().bind();
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0);

//      .bindVertexBufferObject(animatedVBO, BufferTarget.DRAW_INDIRECT)
//      .bindVertexArrayObject(manager.getAnimationArrayObject())
//      .multiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, animationDrawCount, 0)

        glRenderer.getStaticArrayObject().unbind();
        ShadowBuffer.unbindFramebuffer();

        shaderProgram.unbind();
    }

    public void setupBuffers() {
        setupObjectData();
        setupStaticCommandBuffer();
    }

    private void setupObjectData() {
        objectIndexMap.clear();
        int objectIndex = 0;
        for (Model model : glRenderer.getApplication().getCurrentScene().getModels().values()) {
            for (SceneObject object : model.getSceneObjects()) {
                objectIndexMap.put(object.getName(), objectIndex);
                objectIndex++;
            }
        }
    }

    private void setupStaticCommandBuffer() {
        List<Model> models = glRenderer.getApplication().getCurrentScene().getModels().values().stream().filter(m -> !m.isAnimated()).toList();

        int numMeshes = 0;
        int firstIndex = 0;
        int baseInstance = 0;

        for (Model model : models) {
            numMeshes += model.getMeshDrawData().size();
        }

        ByteBuffer commandBuffer = MemoryUtil.memAlloc(numMeshes * 5 * 4);
        for (Model model : models) {
            List<SceneObject> entities = model.getSceneObjects();
            int numEntities = entities.size();
            for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                // count
                commandBuffer.putInt(meshDrawData.vertices());

                // instanceCount
                commandBuffer.putInt(numEntities);
                commandBuffer.putInt(firstIndex);
                // baseVertex
                commandBuffer.putInt(meshDrawData.offset());
                commandBuffer.putInt(baseInstance);

                firstIndex += meshDrawData.vertices();
                baseInstance += entities.size();
            }
        }

        commandBuffer.flip();
        staticDrawCount = commandBuffer.remaining() / 20;

        staticCommandBuffer = new DrawIndirectBuffer();
        staticCommandBuffer.bind();
        staticCommandBuffer.bufferData(commandBuffer, Usage.DYNAMIC_DRAW);

        MemoryUtil.memFree(commandBuffer);
    }

    public Shadows getShadows() {
        return shadows;
    }
}

