package net.ice.relic.engine.opengl;

import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.scene.Scene;
import net.ice.relic.engine.opengl.scene.SceneObject;
import net.ice.relic.engine.opengl.shader.Shader;
import net.ice.relic.engine.opengl.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL43.*;

public class SceneRenderer {

    private ShaderProgram shaderProgram;
    private Uniforms uniforms;

    public SceneRenderer() {
        List<Shader> sceneShaders = new ArrayList<>();
        sceneShaders.add(new Shader("scene.vert", GL_VERTEX_SHADER));
        sceneShaders.add(new Shader("scene.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(sceneShaders);

        createShaderUniforms();
    }

    public void render(Scene scene) {

        shaderProgram.bind();

        Camera camera = scene.getCamera();
        uniforms.setUniform("viewMatrix", camera.getViewMatrix());
        uniforms.setUniform("projectionMatrix", camera.getProjectionMatrix());

        for (SceneObject object : scene.getObjects().values()) {
            uniforms.setUniform("modelMatrix", object.getModelMatrix());
            object.getModel().render(shaderProgram.getProgramID());
        }

        shaderProgram.unbind();
    }

    private void createShaderUniforms() {
        uniforms = new Uniforms(shaderProgram.getProgramID());
        uniforms.createUniform("viewMatrix");
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("modelMatrix");
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }
}

