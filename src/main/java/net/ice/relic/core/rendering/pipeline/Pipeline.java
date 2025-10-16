package net.ice.relic.core.rendering.pipeline;

import net.ice.relic.core.rendering.backend.opengl.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.VertexArrayObject;
import net.ice.relic.core.rendering.backend.opengl.buffer.FrameBufferObject;
import net.ice.relic.core.rendering.backend.opengl.buffer.VertexBufferObject;
import net.ice.relic.core.rendering.backend.opengl.enums.BufferTarget;
import org.joml.*;
import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.util.*;
import java.util.function.Consumer;

import static net.ice.relic.core.rendering.backend.opengl.GLUtil.assertNoError;
import static org.lwjgl.opengl.ARBBindlessTexture.glUniformHandleui64ARB;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL43.glMultiDrawArraysIndirect;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;

public class Pipeline {

    private final List<PipelineCommand> commands = new ArrayList<>();
    private final Map<String, Integer> uniforms = new HashMap<>();
    private boolean built = false;
    private boolean paused = false;
    private int executionIndex = 0;


    public Pipeline() {
    }

    private void addCommand(String name, Consumer<Void> command) {
        commands.add(new PipelineCommand(name, command));
    }

    public void execute() {
        while (executionIndex < commands.size()) {
            PipelineCommand entry = commands.get(executionIndex);
            String cmdName = entry.getName();
            Consumer<Void> command = entry.getCommand();

            try {
                command.accept(null);
            } catch (Exception e) {
                Logger.error("Exception in pipeline command '" + cmdName + "':", e);
            }



            executionIndex++;
            if (paused) break;
        }
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        if (!paused) return;
        this.paused = false;
        execute();
    }

//    public void injectCommand(String name, Consumer<Void> command) {
//        commands.add(name, command);
//    }

    public void reset() {
        executionIndex = 0;
        paused = false;
    }

    public int getUniformLocation(String name) {
        return uniforms.getOrDefault(name, -1);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(getUniformLocation(uniformName), false, value.get(stack.mallocFloat(16)));
            assertNoError();
        }
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(getUniformLocation(uniformName), value);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(getUniformLocation(uniformName), value);
        assertNoError();

    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(getUniformLocation(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector2i value) {
        glUniform2i(getUniformLocation(uniformName), value.x, value.y);
    }

    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(getUniformLocation(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, Vector2f value) {
        glUniform2f(getUniformLocation(uniformName), value.x, value.y);
    }

    public void setUniform(String uniformName, long value) {
        glUniformHandleui64ARB(getUniformLocation(uniformName), value);
    }

    public String formatUniform(String uniformName, int index) {
        return uniformName + "[" + index + "]";
    }

    public static class PipelineBuilder {
        private final Pipeline pipeline = new Pipeline();

        public PipelineBuilder bindShaderProgram(GLShaderProgram shaderProgram) {
            pipeline.addCommand("bindShader", (unused) -> shaderProgram.bind());
            return this;
        }

        public PipelineBuilder unbindShaderProgram(GLShaderProgram shaderProgram) {
            pipeline.addCommand("unbindShader", (unused) -> shaderProgram.unbind());
            return this;
        }

        public PipelineBuilder uniform(String name, GLShaderProgram shaderProgram) {
            int loc = glGetUniformLocation(shaderProgram.getProgramID(), name);
            pipeline.uniforms.put(name, loc);
            return this;
        }

        public PipelineBuilder uniformAssert(String name, GLShaderProgram shaderProgram) {
            int loc = glGetUniformLocation(shaderProgram.getProgramID(), name);
            if (loc < 0) Logger.error("Uniform not found: " + name);
            pipeline.uniforms.put(name, loc);
            return this;
        }

        public PipelineBuilder bindFramebuffer(FrameBufferObject fbo) {
            pipeline.addCommand("bindFramebuffer", (unused) -> {
                fbo.bindFrameBuffer();
            });
            return this;
        }

        public PipelineBuilder multiDrawElementsIndirect(int mode, int type, long indirect, int count, int stride) {
            pipeline.addCommand("multiDrawElementsIndirect", (unused) -> {
                glMultiDrawElementsIndirect(mode, type, indirect, count, stride);
            });
            return this;
        }

        public PipelineBuilder bindVertexBufferObject(VertexBufferObject vertexBufferObject, BufferTarget target) {
            pipeline.addCommand("bindVertexBufferObject", (unused -> {
                vertexBufferObject.bind(target.raw());
            }));
            return this;
        }

        public PipelineBuilder bindVertexArrayObject(VertexArrayObject vertexArrayObject) {
            pipeline.addCommand("bindVertexArrayObject", (unused -> vertexArrayObject.bind()));
            return this;
        }

        public PipelineBuilder unbindVertexArrayObject() {
            pipeline.addCommand("unbindVertexArrayObject", (unused -> {
                glBindVertexArray(0);
            }));
            return this;
        }

        public PipelineBuilder enable(int target) {
            pipeline.addCommand("enable", (unused) -> glEnable(target));
            return this;
        }

        public PipelineBuilder pauseHere() {
            pipeline.addCommand("pause", (unused) -> pipeline.pause());
            return this;
        }

        public Pipeline build() {
            pipeline.built = true;
            return pipeline;
        }
    }


}
