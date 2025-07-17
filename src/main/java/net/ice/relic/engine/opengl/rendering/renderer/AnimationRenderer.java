package net.ice.relic.engine.opengl.rendering.renderer;

import net.ice.relic.RelicApplication;
import net.ice.relic.engine.opengl.Shader;
import net.ice.relic.engine.opengl.model.Animation;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.common.scene.SceneObject;

import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.*;

public class AnimationRenderer extends AbstractRenderer {

    public AnimationRenderer(RelicApplication application) {
        super(application);
    }

    @Override
    protected void initShaders() {
        loadShader("anim.comp", Shader.ShaderType.COMPUTE);
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
                for (RenderingBuffer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                    RenderingBuffer.AnimMeshDrawData animMeshDrawData = meshDrawData.animMeshDrawData();
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

        ensureNoErrorBeforeContinue();
    }

    @Override
    protected void setupData() {

    }


}
