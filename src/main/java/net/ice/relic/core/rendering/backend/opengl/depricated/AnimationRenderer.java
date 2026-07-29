package net.ice.relic.core.rendering.backend.opengl.depricated;

import net.ice.relic.core.rendering.shader.ShaderType;

import static org.lwjgl.opengl.GL43.*;

@Deprecated
public class AnimationRenderer extends AbstractGLRenderer {

    public AnimationRenderer(GLManager manager) {
        super(manager);
    }

    @Override
    protected void initShaders() {
        loadShader("anim.comp", ShaderType.COMPUTE);
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
    public void render() {
        shaderProgram.bind();

//        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, manager.getBindingPoseBuffer().getId());
//        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, manager.getBonesIndicesWeightsBuffer().getId());
//        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, manager.getBonesMatricesBuffer().getId());
//        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, manager.getDestinationAnimationBuffer().getId());

//        int dstOffset = 0;
//        for (Model model : StaticModelComponent.getAllModels()) {
//            if (model.isAnimated()) {
//                for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
//                    GLRenderer.AnimMeshDrawData animMeshDrawData = meshDrawData.animMeshDrawData();
//                    Entity entity = animMeshDrawData.entity();
//                    Animation.AnimatedFrame frame = entity.getAnimationData().getCurrentFrame();
//                    int groupSize = (int) Math.ceil((float) meshDrawData.sizeInBytes() / (14 * 4));
//                    uniforms.setUniform("drawParameters.srcOffset", animMeshDrawData.bindingPoseOffset());
//                    uniforms.setUniform("drawParameters.srcSize", meshDrawData.sizeInBytes() / 4);
//                    uniforms.setUniform("drawParameters.weightsOffset", animMeshDrawData.weightsOffset());
//                    uniforms.setUniform("drawParameters.bonesMatricesOffset", frame.getOffset());
//                    uniforms.setUniform("drawParameters.dstOffset", dstOffset);
//                    glDispatchCompute(groupSize, 1, 1);
//                    dstOffset += meshDrawData.sizeInBytes() / 4;
//                }
//            }
//        }

        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        shaderProgram.unbind();

        //assertNoError();
    }

    @Override
    protected void setupData() {

    }


}
