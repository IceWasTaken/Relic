package net.ice.relic.core.rendering.pipeline;

import net.ice.relic.core.rendering.shader.IShader;
import net.ice.relic.core.rendering.shader.IShaderProgram;

public class PipelineStage {

    private IShaderProgram shaderProgram;

    private PipelineStage() {

    }

    public static class StageBuilder {

        private IShader vertexShader;
        private IShader tesselationShader;
        private IShader geometryShader;
        private IShader fragmentShader;

        private IShader computeShader;


        public StageBuilder vertexShader(IShader shader) {
            this.vertexShader = shader;
            return this;
        }

        public StageBuilder tesselationShader(IShader shader) {
            this.tesselationShader = shader;
            return this;
        }

        public StageBuilder geometryShader(IShader shader) {
            this.geometryShader = shader;
            return this;
        }

        public StageBuilder fragmentShader(IShader shader) {
            this.fragmentShader = shader;
            return this;
        }

        public StageBuilder computeShader(IShader shader) {
            this.computeShader = shader;
            return this;
        }

        public PipelineStage build() {
            return new PipelineStage();
        }
    }

}
