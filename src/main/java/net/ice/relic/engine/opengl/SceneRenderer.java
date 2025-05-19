package net.ice.relic.engine.opengl;

import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.scene.Scene;
import net.ice.relic.engine.opengl.shader.Shader;
import net.ice.relic.engine.opengl.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL43.*;

public class SceneRenderer {

    private ShaderProgram shaderProgram;
    private Uniforms uniforms;
    private int staticModelDrawCount;

    public SceneRenderer() {
        List<Shader> sceneShaders = new ArrayList<>();
        sceneShaders.add(new Shader("scene.vert", GL_VERTEX_SHADER));
        sceneShaders.add(new Shader("scene.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(sceneShaders);
    }

    public void render(Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_BLEND);

        shaderProgram.bind();

        uniforms.setUniform("projectionMatrix", scene.getCamera().getProjectionMatrix());
        uniforms.setUniform("viewMatrix", scene.getCamera().getViewMatrix());

        int drawCount = 0;
        for(Model model : Scene.models.values()) {

        }
        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, );
        glBindVertexArray();
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticModelDrawCount, 0);

        glBindVertexArray(0);
        glEnable(GL_BLEND);
        shaderProgram.unbind();
    }

    private void createShaderUniforms() {
        uniforms = new Uniforms(shaderProgram.getProgramID());
        uniforms.createUniform("viewMatrix");
        uniforms.createUniform("projectionMatrix");
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }


}

