package net.ice.relic.engine.opengl.rendering.renderer;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.RelicApplication;
import net.ice.relic.engine.opengl.*;
import net.ice.relic.engine.opengl.rendering.GeometryBuffer;
import net.ice.relic.common.scene.Fog;
import net.ice.relic.common.scene.Lights;
import net.ice.relic.common.scene.Scene;
import net.ice.relic.common.scene.light.AmbientLight;
import net.ice.relic.common.scene.light.DirLight;
import net.ice.relic.common.scene.light.PointLight;
import net.ice.relic.common.scene.light.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

@Rewrite
public class LightRenderer extends AbstractRenderer {

    private QuadMesh quadMesh;
    private ShadowRenderer shadowRenderer;

    public LightRenderer(RelicApplication application) {
        super(application);
    }

    @Override
    public void init(RenderingBuffer renderingBuffer, GeometryBuffer buffer) {
        super.init(renderingBuffer, buffer);
        this.quadMesh = new QuadMesh();
    }

    @Override
    protected void initShaders() {
        loadShader("lights.vert", Shader.ShaderType.VERTEX);
        loadShader("lights.frag", Shader.ShaderType.FRAGMENT);
    }

    @Override
    protected void initUniforms() {
        uniforms.createUniform("albedoSampler");
        uniforms.createUniform("normalSampler");
        uniforms.createUniform("specularSampler");
        uniforms.createUniform("depthSampler");

        uniforms.createUniform("invProjectionMatrix");
        uniforms.createUniform("invViewMatrix");
        uniforms.createUniform("ambientLight.factor");
        uniforms.createUniform("ambientLight.color");

        for (int i = 0; i < config.getMaxPointLights(); i++) {
            String prefix = uniforms.formatUniform("pointLights", i);
            uniforms.createUniform(prefix + ".position");
            uniforms.createUniform(prefix + ".color");
            uniforms.createUniform(prefix + ".intensity");
            uniforms.createUniform(prefix + ".attenuation.constant");
            uniforms.createUniform(prefix + ".attenuation.linear");
            uniforms.createUniform(prefix + ".attenuation.exponent");
        }

        for (int i = 0; i < config.getMaxSpotLights(); i++) {
            String prefix = uniforms.formatUniform("spotLights", i);
            uniforms.createUniform(prefix + ".pl.position");
            uniforms.createUniform(prefix + ".pl.color");
            uniforms.createUniform(prefix + ".pl.intensity");
            uniforms.createUniform(prefix + ".pl.attenuation.constant");
            uniforms.createUniform(prefix + ".pl.attenuation.linear");
            uniforms.createUniform(prefix + ".pl.attenuation.exponent");
            uniforms.createUniform(prefix + ".direction");
            uniforms.createUniform(prefix + ".cutoff");
        }

        uniforms.createUniform("directionalLight.direction");
        uniforms.createUniform("directionalLight.color");
        uniforms.createUniform("directionalLight.intensity");

        uniforms.createUniform("fog.activeFog");
        uniforms.createUniform("fog.color");
        uniforms.createUniform("fog.density");

        for (int i = 0; i < Shadow.SHADOW_MAP_COUNT; i++) {
            String prefix = uniforms.formatUniform("shadowMap", i);
            uniforms.createUniform("shadowMap_" + i);
            uniforms.createUniform(prefix + ".shadowProjectionMatrix");
            uniforms.createUniform(prefix + ".splitDistance");
        }
    }

    @Override
    protected void render() {
        Scene scene = application.getCurrentScene();

        shaderProgram.bind();

        updateLights();

        // Bind the G-Buffer textures
        int[] textureIds = geometryBuffer.getTextureIDS();
        int numTextures = textureIds != null ? textureIds.length : 0;
        for (int i = 0; i < numTextures; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, textureIds[i]);
        }

        uniforms.setUniform("albedoSampler", 0);
        uniforms.setUniform("normalSampler", 1);
        uniforms.setUniform("specularSampler", 2);
        uniforms.setUniform("depthSampler", 3);

        Fog fog = scene.getFog();
        uniforms.setUniform("fog.activeFog", fog.isActive() ? 1 : 0);
        uniforms.setUniform("fog.color", fog.getColor());
        uniforms.setUniform("fog.density", fog.getDensity());

        int start = 4;
        List<Shadow> cascadeShadows = shadowRenderer.getShadows();
        for (int i = 0; i < Shadow.SHADOW_MAP_COUNT; i++) {
            glActiveTexture(GL_TEXTURE0 + start + i);
            uniforms.setUniform("shadowMap_" + i, start + i);
            Shadow cascadeShadow = cascadeShadows.get(i);
            uniforms.setUniform("shadowMap[" + i + "]" + ".shadowProjectionMatrix", cascadeShadow.getProjectionMatrix());
            uniforms.setUniform("shadowMap[" + i + "]" + ".splitDistance", cascadeShadow.getShadowDistance());
        }
        shadowRenderer.getShadowBuffer().bindTextures(GL_TEXTURE0 + start);

        uniforms.setUniform("invProjectionMatrix", scene.getMatrix().getInvProjMatrix());
        uniforms.setUniform("invViewMatrix", scene.getCamera().getViewMatrix().invert());

        quadMesh.getMeshVAO().bind();
        glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        shaderProgram.unbind();
        ensureNoErrorBeforeContinue();
    }

    public void updateLights() {
        Matrix4f viewMatrix = application.getCurrentScene().getCamera().getViewMatrix();

        Lights lights = application.getCurrentScene().getLights();
        AmbientLight ambientLight = lights.getAmbientLight();
        uniforms.setUniform("ambientLight.factor", ambientLight.getIntensity());
        uniforms.setUniform("ambientLight.color", ambientLight.getColor());

        DirLight dirLight = application.getCurrentScene().getLights().getDirLight();
        Vector4f auxDirection = new Vector4f(dirLight.getDirection(), 0);
        auxDirection.mul(viewMatrix);
        Vector3f direction = new Vector3f(auxDirection.x, auxDirection.y, auxDirection.z);
        uniforms.setUniform("directionalLight.color", dirLight.getColor());
        uniforms.setUniform("directionalLight.direction", direction);
        uniforms.setUniform("directionalLight.intensity", dirLight.getIntensity());


        List<PointLight> pointLights = application.getCurrentScene().getLights().getPointLights();
        int numPointLights = pointLights.size();
        PointLight pointLight;
        for (int i = 0; i < config.getMaxPointLights(); i++) {
            if (i < numPointLights) {
                pointLight = pointLights.get(i);
            } else {
                pointLight = null;
            }
            String name = uniforms.formatUniform("pointLights", i);
            updatePointLight(pointLight, name, viewMatrix);
        }

        List<SpotLight> spotLights = application.getCurrentScene().getLights().getSpotLights();
        int numSpotLights = spotLights.size();
        SpotLight spotLight;
        for (int i = 0; i < config.getMaxSpotLights(); i++) {
            if (i < numSpotLights) {
                spotLight = spotLights.get(i);
            } else {
                spotLight = null;
            }
            String prefix = uniforms.formatUniform("spotLights", i);
            updateSpotLight(spotLight, prefix, viewMatrix);
        }
    }

    private void updatePointLight(PointLight pointLight, String prefix, Matrix4f viewMatrix) {
        Vector4f aux = new Vector4f();
        Vector3f lightPosition = new Vector3f();
        Vector3f color = new Vector3f();
        float intensity = 0.0f;
        float constant = 0.0f;
        float linear = 0.0f;
        float exponent = 0.0f;
        if (pointLight != null) {
            aux.set(pointLight.getPosition(), 1);
            aux.mul(viewMatrix);
            lightPosition.set(aux.x, aux.y, aux.z);
            color.set(pointLight.getColor());
            intensity = pointLight.getIntensity();
            PointLight.Attenuation attenuation = pointLight.getAttenuation();
            constant = attenuation.getConstant();
            linear = attenuation.getLinear();
            exponent = attenuation.getExponent();
        }
        uniforms.setUniform(prefix + ".position", lightPosition);
        uniforms.setUniform(prefix + ".color", color);
        uniforms.setUniform(prefix + ".intensity", intensity);
        uniforms.setUniform(prefix + ".attenuation.constant", constant);
        uniforms.setUniform(prefix + ".attenuation.linear", linear);
        uniforms.setUniform(prefix + ".attenuation.exponent", exponent);
    }

    private void updateSpotLight(SpotLight spotLight, String prefix, Matrix4f viewMatrix) {
        PointLight pointLight = null;
        Vector3f coneDirection = new Vector3f();
        float cutoff = 0.0f;
        if (spotLight != null) {
            coneDirection = spotLight.getConeDirection();
            cutoff = spotLight.getCutOff();
            pointLight = spotLight.getPointLight();
        }

        uniforms.setUniform(prefix + ".direction", coneDirection);
        uniforms.setUniform(prefix + ".cutoff", cutoff);
        updatePointLight(pointLight, prefix + ".pl", viewMatrix);
    }

    public void setShadowRenderer(ShadowRenderer shadowRenderer) {
        this.shadowRenderer = shadowRenderer;
    }


    @Override
    protected void setupData() {

    }
}