package net.ice.relic.engine.opengl;

import net.ice.relic.engine.opengl.scene.Scene;
import net.ice.relic.engine.opengl.shader.Shader;
import net.ice.relic.engine.opengl.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL43.*;

public class SceneRenderer {

    private ShaderProgram shaderProgram;

    public SceneRenderer() {
        List<Shader> sceneShaders = new ArrayList<>();
        sceneShaders.add(new Shader("scene.vert", GL_VERTEX_SHADER));
        sceneShaders.add(new Shader("scene.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(sceneShaders);
    }

    public void render(Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_BLEND);


        glBindVertexArray(0);
        glEnable(GL_BLEND);
        shaderProgram.bind();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }


}

