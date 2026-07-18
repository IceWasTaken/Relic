package net.ice.relic.core.rendering.pipeline;

import net.ice.curio.library.opengl.wrapper.enums.FramebufferTarget;
import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.buffer.DrawIndirectBuffer;
import net.ice.curio.library.opengl.object.buffer.VertexBufferObject;
import org.tinylog.Logger;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.curio.library.opengl.object.framebuffer.FramebufferObject;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.util.*;
import java.util.function.Consumer;

import static org.lwjgl.opengl.ARBBindlessTexture.glUniformHandleui64ARB;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;

@Deprecated
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
        }
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(getUniformLocation(uniformName), value);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(getUniformLocation(uniformName), value);
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
            pipeline.addCommand("unbindShader", (unused) -> {

            });
            return this;
        }

        public PipelineBuilder uniform(String name, GLShaderProgram shaderProgram) {
            int loc = glGetUniformLocation(shaderProgram.getProgramID(), name);
            if (loc < 0) Logger.error("Uniform not found: " + name);
            pipeline.uniforms.put(name, loc);

            return this;
        }
        public PipelineBuilder uniformAssert(String name, GLShaderProgram shaderProgram) {
            int loc = glGetUniformLocation(shaderProgram.getProgramID(), name);
            if (loc < 0) Logger.error("Uniform not found: " + name);
            pipeline.uniforms.put(name, loc);
            return this;
        }

        public PipelineBuilder bindFramebuffer(FramebufferObject fbo, FramebufferTarget framebufferTarget) {
            pipeline.addCommand("bindFramebuffer", (unused) -> {
                fbo.bind(framebufferTarget);
            });
            return this;
        }
        public PipelineBuilder unbindFramebuffer(FramebufferObject fbo, FramebufferTarget framebufferTarget) {
            pipeline.addCommand("unbindFramebuffer", (unused) -> {
                fbo.unbind(framebufferTarget);
            });
            return this;
        }

        public PipelineBuilder drawElements(int mode, int count, int type, long indices) {
            pipeline.addCommand("drawElements", (unused) -> {
                glDrawElements(mode, count, type, indices);
            });
            return this;
        }
        public PipelineBuilder multiDrawElementsIndirect(int mode, int type, long indirect, int count, int stride) {
            pipeline.addCommand("multiDrawElementsIndirect", (unused) -> {
                glMultiDrawElementsIndirect(mode, type, indirect, count, stride);
            });
            return this;
        }

        public PipelineBuilder bindVertexBufferObject(VertexBufferObject vertexBufferObject) {
            pipeline.addCommand("bindVertexBufferObject", (unused -> {
                vertexBufferObject.bind();
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

        public PipelineBuilder bindDrawIndirectBuffer(DrawIndirectBuffer drawIndirectBuffer) {
            pipeline.addCommand("bindDrawIndirectBuffer", (unused -> drawIndirectBuffer.bind()));
            return this;
        }
        public PipelineBuilder unbindDrawIndirectBuffer() {
            pipeline.addCommand("unbindDrawIndirectBuffer", (unused -> {
                glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0);
            }));
            return this;
        }

        public PipelineBuilder enable(int target) {
            pipeline.addCommand("enable", (unused) -> glEnable(target));
            return this;
        }
        public PipelineBuilder disable(int target) {
            pipeline.addCommand("disable", (unused) -> glDisable(target));
            return this;
        }
        public PipelineBuilder clear(int target) {
            pipeline.addCommand("clear", (unused) -> glClear(target));
            return this;
        }

        public PipelineBuilder clearColor(float r, float g, float b, float a) {
            pipeline.addCommand("clearColor", (unused) -> glClearColor(r, g, b, a));
            return this;
        }

        public PipelineBuilder pauseHere() {
            pipeline.addCommand("pause", (unused) -> pipeline.pause());
            return this;
        }

        public PipelineBuilder customCommand(String name, Consumer<Void> action) {
            pipeline.addCommand(name, action);
            return this;
        }

        public Pipeline build() {
            pipeline.built = true;
            return pipeline;
        }
    }


}
