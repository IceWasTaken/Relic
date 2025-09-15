package net.ice.relic.core.rendering;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.opengl.GLShader;
import net.ice.relic.core.rendering.backend.opengl.model.Animation;
import net.ice.relic.core.rendering.backend.opengl.model.Model;
import net.ice.relic.core.scene.SceneObject;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.RenderingBuffers;

import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.*;

public class AnimationRenderer extends AbstractRenderer {

    public AnimationRenderer(RelicApplication application) {
        super(application);
    }

    @Override
    protected void initShaders() {
        loadShader("anim.comp", GLShader.ShaderType.COMPUTE);
    }

    @Override
    protected void initUniforms() {
        uniforms.createUniform("drawParameters.srcOffset");
        uniforms.createUniform("drawParameters.srcSize");
        uniforms.createUniform("drawParameters.weightsOffset");
        uniforms.createUniform("drawParameters.bonesMatricesOffset");
        uniforms.createUniform("drawParameters.dstOffset");
    }

    @Override
    protected void render() {
        shaderProgram.bind();

        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, renderingBuffer.getBindingPoseBuffer().getId());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, renderingBuffer.getBonesIndicesWeightsBuffer().getId());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, renderingBuffer.getBonesMatricesBuffer().getId());
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, renderingBuffer.getDestinationAnimationBuffer().getId());

        int dstOffset = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            if (model.isAnimated()) {
                for (RenderingBuffers.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                    RenderingBuffers.AnimMeshDrawData animMeshDrawData = meshDrawData.animMeshDrawData();
                    SceneObject entity = animMeshDrawData.entity();
                    Animation.AnimatedFrame frame = entity.getAnimationData().getCurrentFrame();
                    int groupSize = (int) Math.ceil((float) meshDrawData.sizeInBytes() / (14 * 4));
                    uniforms.setUniform("drawParameters.srcOffset", animMeshDrawData.bindingPoseOffset());
                    uniforms.setUniform("drawParameters.srcSize", meshDrawData.sizeInBytes() / 4);
                    uniforms.setUniform("drawParameters.weightsOffset", animMeshDrawData.weightsOffset());
                    uniforms.setUniform("drawParameters.bonesMatricesOffset", frame.getOffset());
                    uniforms.setUniform("drawParameters.dstOffset", dstOffset);
                    glDispatchCompute(groupSize, 1, 1);
                    dstOffset += meshDrawData.sizeInBytes() / 4;
                }
            }
        }

        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        shaderProgram.unbind();

        assertNoError();
    }

    @Override
    protected void setupData() {

    }


}
