package net.ice.relic.core.model;

import net.ice.relic.common.util.AssimpUtil;
import net.ice.relic.core.cache.TextureCache;
import net.ice.relic.core.rendering.backend.opengl.model.texture.GLTexture;
import net.ice.relic.common.util.ColorUtil;
import net.ice.relic.core.resource.Resource;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;

import static net.ice.relic.common.util.AssimpUtil.getMaterialColor;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHININESS_STRENGTH;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiGetMaterialFloatArray;
import static org.lwjgl.assimp.Assimp.aiGetMaterialTexture;
import static org.lwjgl.assimp.Assimp.aiReturn_SUCCESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NORMALS;

public class Material {

    public static final ColorUtil.Color DEFAULT_COLOR = new ColorUtil.Color(0.0f, 0.0f, 0.0f, 1.0f);

    /**
     * Size in bytes of the Material structure:
     * <ul>
     *   <li>Diffuse and specular colors: 2 × vec4 (4 floats each) = <b>32 bytes</b></li>
     *   <li>Reflectance: 1 × float = <b>4 bytes</b> <i>(deprecated soon)</i></li>
     *   <li>Texture handles: 5 × uint64_t (8 bytes each) = <b>40 bytes</b></li>
     * </ul>
     * <b>Total: 76 bytes</b>
     */
    public static final int MATERIAL_SIZE = 76;

    private int materialIndex;
    private float reflectance;
    private float emissiveStrength;

    private ColorUtil.Color diffuseColor;
    private ColorUtil.Color specularColor;

    @Deprecated
    private ColorUtil.Color ambientColor;
    @Deprecated
    private ColorUtil.Color emissiveColor;

    private String texturePath;
    private String normalMapPath;
    private String emissiveMapPath;
    private String specularMapPath;
    private String AOMapPath;

    private GLTexture texture;
    private GLTexture normalMap;
    private GLTexture emissiveMap;
    private GLTexture specularMap;
    private GLTexture AOMap;

    public Material() {
        this.ambientColor = DEFAULT_COLOR;
        this.diffuseColor = DEFAULT_COLOR;
        this.specularColor = DEFAULT_COLOR;
        this.emissiveColor = DEFAULT_COLOR;
        this.materialIndex = 0;
    }

    public static Material processMaterial(AIMaterial aiMaterial, String directory, TextureCache textureCache) {
        Material material = new Material();
        float[] shininess = new float[]{0.0f};
        int[] max = new int[]{1};
        float reflectance = 0.0f;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            material.setAmbientColor(getMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT).convertFromOpenGLColor());
            material.setDiffuseColor(getMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE).convertFromOpenGLColor());
            material.setSpecularColor(getMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR).convertFromOpenGLColor());
            material.setEmissiveColor(getMaterialColor(aiMaterial, AI_MATKEY_COLOR_EMISSIVE).convertFromOpenGLColor());

            //dumb
            int shininessResult = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0, shininess, max);
            if (shininessResult != aiReturn_SUCCESS) {
                reflectance = shininess[0];
            }
            material.setReflectance(reflectance);

            AIString aiTexturePath = AIString.calloc(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();
            if (!texturePath.isEmpty()) {
                material.setTexturePath(directory + File.separator + "textures/" + new File(texturePath).getName());
                GLTexture texture = textureCache.createTexture(Resource.getResourceWithDefaultNamespace(material.getTexturePath()));
                material.setDiffuseColor(Material.DEFAULT_COLOR);
                material.setTexture(texture);
            }

            String texPath;
            if (!(texPath = AssimpUtil.getTexturePath(aiMaterial, aiTextureType_NORMALS)).isEmpty()) {
                material.setNormalMapPath(directory + File.separator + "textures/" + new File(texPath).getName());
                material.setNormalMap(textureCache.createTexture(Resource.getResourceWithDefaultNamespace(material.getNormalMapPath())));
            }

            if (!(texPath = AssimpUtil.getTexturePath(aiMaterial, aiTextureType_EMISSIVE)).isEmpty()) {
                material.setEmissiveMapPath(directory + File.separator + "textures/" + new File(texPath).getName());
                material.setEmissiveMap(textureCache.createTexture(Resource.getResourceWithDefaultNamespace(material.getEmissiveMapPath())));
            }

//            if (!(texPath = AssimpUtil.getTexturePath(aiMaterial, aiTextureType_SPECULAR, 1)).isEmpty()) {
//                material.setSpecularMapPath(directory + File.separator + "textures/" + new File(texPath).getName());
//                material.setSpecularMap(textureCache.createTexture(Resource.getResourceWithDefaultNamespace(material.getNormalMapPath())));
//            }
//
//            if (!(texPath = AssimpUtil.getTexturePath(aiMaterial, aiTextureType_AMBIENT, 1)).isEmpty()) {
//                material.setAOMapPath(directory + File.separator + "textures/" + new File(texPath).getName());
//                material.setAOMap(textureCache.createTexture(Resource.getResourceWithDefaultNamespace(material.getNormalMapPath())));
//            }

            return material;
        }
    }

    public ColorUtil.Color getAmbientColor() { return ambientColor; }
    public ColorUtil.Color getDiffuseColor() { return diffuseColor; }
    public ColorUtil.Color getSpecularColor() { return specularColor; }
    public ColorUtil.Color getEmissiveColor() { return emissiveColor; }

    public float getReflectance() { return reflectance; }
    public float getEmissiveStrength() { return emissiveStrength; }

    public String getTexturePath() { return texturePath; }
    public String getNormalMapPath() { return normalMapPath; }
    public String getEmissiveMapPath() {
        return emissiveMapPath;
    }

    public int getMaterialIndex() { return materialIndex; }

    public GLTexture getTexture() { return texture; }
    public GLTexture getNormalMap() { return normalMap; }
    public GLTexture getEmissiveMap() {
        return emissiveMap;
    }
    public GLTexture getSpecularMap() {
        return specularMap;
    }
    public GLTexture getAOMap() {
        return AOMap;
    }

    public boolean hasTexture() { return texture != null; }
    public boolean hasNormalMap() { return normalMap != null; }
    public boolean hasEmissiveMap() { return emissiveMap != null; }
    public boolean hasSpecularMap() { return specularMap != null; }
    public boolean hasAOMap() { return AOMap != null; }

    public long getTextureHandle() {
        return hasTexture() ? texture.getBindlessHandle() : 0L;
    }

    public long getNormalHandle() {
        return hasNormalMap() ? normalMap.getBindlessHandle() : 0L;
    }

    public long getEmissiveHandle() {
        return hasEmissiveMap() ? emissiveMap.getBindlessHandle() : 0L;
    }

    public long getSpecularHandle() {
        return hasSpecularMap() ? specularMap.getBindlessHandle() : 0L;
    }

    public long getAOHandle() {
        return hasAOMap() ? AOMap.getBindlessHandle() : 0L;
    }

    public void setAmbientColor(ColorUtil.Color ambientColor) { this.ambientColor = ambientColor; }
    public void setDiffuseColor(ColorUtil.Color diffuseColor) { this.diffuseColor = diffuseColor; }
    public void setSpecularColor(ColorUtil.Color specularColor) { this.specularColor = specularColor; }
    public void setEmissiveColor(ColorUtil.Color emissiveColor) { this.emissiveColor = emissiveColor; }

    public void setReflectance(float reflectance) { this.reflectance = reflectance; }
    public void setEmissiveStrength(float emissiveStrength) { this.emissiveStrength = emissiveStrength; }

    public void setTexturePath(String texturePath) { this.texturePath = texturePath; }
    public void setNormalMapPath(String normalMapPath) { this.normalMapPath = normalMapPath; }
    public void setEmissiveMapPath(String emissiveMapPath) {
        this.emissiveMapPath = emissiveMapPath;
    }
    public void setSpecularMapPath(String specularMapPath) {
        this.specularMapPath = specularMapPath;
    }
    public void setAOMapPath(String AOMapPath) {
        this.AOMapPath = AOMapPath;
    }

    public void setMaterialIndex(int materialIndex) { this.materialIndex = materialIndex; }

    public void setTexture(GLTexture texture) { this.texture = texture; }
    public void setNormalMap(GLTexture normalMap) { this.normalMap = normalMap; }
    public void setEmissiveMap(GLTexture emissiveMap) {
        this.emissiveMap = emissiveMap;
    }
    public void setSpecularMap(GLTexture specularMap) {
        this.specularMap = specularMap;
    }
    public void setAOMap(GLTexture AOMap) {
        this.AOMap = AOMap;
    }
}
