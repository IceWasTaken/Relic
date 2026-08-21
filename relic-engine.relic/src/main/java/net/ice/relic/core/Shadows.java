package net.ice.relic.core;

import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Shadows {

    public static final int SHADOW_MAP_COUNT = 3;
    private final List<ShadowData> shadowData;

    private static final float LAMBDA = 0.95f;
    private static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
    private static final Vector3f UP_ALT = new Vector3f(0.0f, 0.0f, 1.0f);

    public Shadows() {
        shadowData = new ArrayList<>();
        for (int i = 0; i < SHADOW_MAP_COUNT; i++) {
            shadowData.add(new ShadowData());
        }
    }

    public void update(Scene scene) {
        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();
        Matrix4f projMatrix = scene.getMatrix().getProjMatrix();
        List<Light> lights = scene.getLights();
        Light dirLight = null;
        for (Light light : lights) {
            if (light.isDirectional()) {
                dirLight = light;
                break;
            }
        }
        if (dirLight == null) {
            throw new RuntimeException("Could not find directional light");
        }

        Vector4f lightPos = new Vector4f(dirLight.getPosition(), 0);

        float[] cascadeSplits = new float[SHADOW_MAP_COUNT];

        float nearClip = ProjectionMatrix.Z_NEAR;
        float farClip = ProjectionMatrix.Z_FAR;
        float clipRange = farClip - nearClip;

        float minZ = nearClip;
        float maxZ = nearClip + clipRange;

        float range = maxZ - minZ;
        float ratio = maxZ / minZ;

        List<ShadowData> cascadeDataList = this.getShadowData();
        int numCascades = cascadeDataList.size();

        // Calculate split depths based on view camera frustum
        // Based on method presented in https://developer.nvidia.com/gpugems/GPUGems3/gpugems3_ch10.html
        for (int i = 0; i < numCascades; i++) {
            float p = (i + 1) / (float) (SHADOW_MAP_COUNT);
            float log = (float) (minZ * Math.pow(ratio, p));
            float uniform = minZ + range * p;
            float d = LAMBDA * (log - uniform) + uniform;
            cascadeSplits[i] = (d - nearClip) / clipRange;
        }

        // Calculate orthographic projection matrix for each cascade
        float lastSplitDist = 0.0f;
        for (int i = 0; i < numCascades; i++) {
            float splitDist = cascadeSplits[i];

            Vector3f[] frustumCorners = new Vector3f[]{
                    new Vector3f(-1.0f,  1.0f, -1.0f),
                    new Vector3f( 1.0f,  1.0f, -1.0f),
                    new Vector3f( 1.0f, -1.0f, -1.0f),
                    new Vector3f(-1.0f, -1.0f, -1.0f),

                    new Vector3f(-1.0f,  1.0f, 1.0f),
                    new Vector3f( 1.0f,  1.0f, 1.0f),
                    new Vector3f( 1.0f, -1.0f, 1.0f),
                    new Vector3f(-1.0f, -1.0f, 1.0f),
            };

            // Project frustum corners into world space
            Matrix4f invCam = (new Matrix4f(projMatrix).mul(viewMatrix)).invert();
            for (int j = 0; j < 8; j++) {
                Vector4f invCorner = new Vector4f(frustumCorners[j], 1.0f).mul(invCam);
                frustumCorners[j] = new Vector3f(invCorner.x, invCorner.y, invCorner.z).div(invCorner.w);
            }

            for (int j = 0; j < 4; j++) {
                Vector3f dist = new Vector3f(frustumCorners[j + 4]).sub(frustumCorners[j]);
                frustumCorners[j + 4] = new Vector3f(frustumCorners[j]).add(new Vector3f(dist).mul(splitDist));
                frustumCorners[j] = new Vector3f(frustumCorners[j]).add(new Vector3f(dist).mul(lastSplitDist));
            }

            // Get frustum center
            Vector3f frustumCenter = new Vector3f(0.0f);
            for (int j = 0; j < 8; j++) {
                frustumCenter.add(frustumCorners[j]);
            }
            frustumCenter.div(8.0f);

            var up = UP;
            float sphereRadius = 0.0f;
            for (int j = 0; j < 8; j++) {
                float dist = new Vector3f(frustumCorners[j]).sub(frustumCenter).length();
                sphereRadius = Math.max(sphereRadius, dist);
            }
            sphereRadius = (float) Math.ceil(sphereRadius * 16.0f) / 16.0f;

            Vector3f maxExtents = new Vector3f(sphereRadius);
            Vector3f minExtents = new Vector3f(maxExtents).mul(-1.0f);

            Vector3f lightDir = new Vector3f(lightPos.x, lightPos.y, lightPos.z);
            Vector3f shadowCameraPos = new Vector3f(frustumCenter).add(lightDir.mul(minExtents.z));

            float dot = Math.abs(new Vector3f(lightPos.x, lightPos.y, lightPos.z).dot(up));
            if (dot == 1.0f) {
                up = UP_ALT;
            }

            Matrix4f lightViewMatrix = new Matrix4f().lookAt(shadowCameraPos, frustumCenter, up);
            Matrix4f lightOrthoMatrix = new Matrix4f().ortho
                    (minExtents.x, maxExtents.x, minExtents.y, maxExtents.y, 0.0f, maxExtents.z - minExtents.z, true);


            // Stabilize shadow
            int shadowMapSize = 4096;
            Vector4f shadowOrigin = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
            lightViewMatrix.transform(shadowOrigin);
            shadowOrigin.mul(shadowMapSize / 2.0f);

            Vector4f roundedOrigin = new Vector4f(shadowOrigin).round();
            Vector4f roundOffset = roundedOrigin.sub(shadowOrigin);
            roundOffset.mul(2.0f / shadowMapSize);
            roundOffset.z = 0.0f;
            roundOffset.w = 0.0f;

            lightOrthoMatrix.m30(lightOrthoMatrix.m30() + roundOffset.x);
            lightOrthoMatrix.m31(lightOrthoMatrix.m31() + roundOffset.y);
            lightOrthoMatrix.m32(lightOrthoMatrix.m32() + roundOffset.z);
            lightOrthoMatrix.m33(lightOrthoMatrix.m33() + roundOffset.w);

            ShadowData cascadeData = cascadeDataList.get(i);
            cascadeData.setSplitDistance((nearClip + splitDist * clipRange) * -1.0f);
            cascadeData.setProjViewMatrix(lightOrthoMatrix.mul(lightViewMatrix));

            lastSplitDist = cascadeSplits[i];
        }
    }

    public List<ShadowData> getShadowData() {
        return shadowData;
    }
}
