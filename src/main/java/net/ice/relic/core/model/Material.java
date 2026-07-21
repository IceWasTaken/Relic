package net.ice.relic.core.model;

import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.heirloom.color.RGBColor;
import net.ice.relic.core.cache.TextureCache;
import net.ice.heirloom.io.resource.Resource;
import org.joml.Vector4f;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AITexture;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;

import static net.ice.relic.common.util.AssimpUtil.getMaterialColor;
import static org.lwjgl.assimp.Assimp.*;

public class Material {

    public static final RGBColor DEFAULT_COLOR = new RGBColor(0.0f, 0.0f, 0.0f, 1.0f);

    /**
     * Size in bytes of the Material structure:
     * <ul>
     *   <li>Diffuse and specular colors: 2 × vec4 (4 floats each) = <b>32 bytes</b></li>
     *   <li>Reflectance: 1 × float = <b>4 bytes</b></li>
     *   <li>Roughness: 1 × float = <b>4 bytes</b></li>
     *   <li>Metallic-ness: 1 × <b>4 bytes</b></li>
     *   <li>Texture handles: 3 × uint64_t (8 bytes extends Struct each) = <b>24 bytes</b></li>
     * </ul>
     * <b>Total: 68 bytes</b>
     */
    public static final int MATERIAL_SIZE = 68;

    private int materialIndex;
    private float reflectance;
    private float emissiveStrength;
    private float roughnessFactor;
    private float metallicFactor;

    private RGBColor diffuseColor;
    private RGBColor specularColor;

    private RGBColor ambientColor;
    private RGBColor emissiveColor;

    private Resource texturePath;
    private Resource normalMapPath;
    private Resource roughnessMapPath;

    private Texture texture;
    private Texture normalMap;
    private Texture roughnessMap;

    public Material() {
        this.ambientColor = DEFAULT_COLOR;
        this.diffuseColor = DEFAULT_COLOR;
        this.specularColor = DEFAULT_COLOR;
        this.emissiveColor = DEFAULT_COLOR;
        this.materialIndex = 0;
    }

    public static Material processMaterial(AIScene aiScene, AIMaterial aiMaterial, String directory, TextureCache textureCache) {
        Material material = new Material();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            material.setAmbientColor(getMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT).mult());
            material.setDiffuseColor(getMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE).mult());
            material.setSpecularColor(getMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR).mult());
            material.setEmissiveColor(getMaterialColor(aiMaterial, AI_MATKEY_COLOR_EMISSIVE).mult());

            material.setReflectance(Material.getFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH));
            material.setRoughnessFactor(Material.getFloatArray(aiMaterial, AI_MATKEY_ROUGHNESS_FACTOR));
            material.setMetallicFactor(Material.getFloatArray(aiMaterial, AI_MATKEY_METALLIC_FACTOR));

            AIString aiTexturePath = AIString.calloc(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();

            if(!texturePath.isEmpty()) {
                material.setTexturePath(Resource.getResource("relic", "assets/textures/" + new File(texturePath).getName()));
                material.setTexture(textureCache.createTexture(material.getTexturePath()));
            }

            aiTexturePath = AIString.calloc(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
            texturePath = aiTexturePath.dataString();
            if(!texturePath.isEmpty()) {
                material.setNormalMapPath(Resource.getResource("relic", "assets/textures/" + new File(texturePath).getName()));
                material.setNormalMap(textureCache.createTexture(material.getNormalMapPath()));
            }

            aiTexturePath = AIString.calloc(stack);
            aiGetMaterialTexture(aiMaterial, 27, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
            texturePath = aiTexturePath.dataString();

            if(!texturePath.isEmpty()) {
                material.setRoughnessMapPath(Resource.getResource("relic", "assets/textures/" + new File(texturePath).getName()));
                material.setRoughnessMap(textureCache.createTexture(material.getRoughnessMapPath()));
            }
            return material;
        }
    }

    private static float getFloatArray(AIMaterial aiMaterial, String type) {
        float[] returns = new float[]{0.0f};
        if (aiGetMaterialFloatArray(aiMaterial, type, aiTextureType_NONE, 0, returns, new int[]{1}) != aiReturn_SUCCESS) {
            return returns[0];
        }
        return returns[0];
    }

    private static AITexture getInternalTexture(AIScene aiScene, int index) {
        return AITexture.create(aiScene.mTextures().get(index));
    }

    private static boolean isInternalTexture(String path) {
        return path.startsWith("*");
    }

    public RGBColor getAmbientColor() { return ambientColor; }
    public RGBColor getDiffuseColor() { return diffuseColor; }
    public RGBColor getSpecularColor() { return specularColor; }
    public RGBColor getEmissiveColor() { return emissiveColor; }

    public float getReflectance() { return reflectance; }
    public float getEmissiveStrength() { return emissiveStrength; }
    public float getMetallicFactor() {
        return metallicFactor;
    }
    public float getRoughnessFactor() {
        return roughnessFactor;
    }

    public Resource getTexturePath() { return texturePath; }
    public Resource getNormalMapPath() { return normalMapPath; }
    public Resource getRoughnessMapPath() {
        return roughnessMapPath;
    }

    public int getMaterialIndex() { return materialIndex; }

    public Texture getTexture() { return texture; }
    public Texture getNormalMap() { return normalMap; }
    public Texture getRoughnessMap() {
        return roughnessMap;
    }

    public boolean hasTexture() { return texture != null; }
    public boolean hasNormalMap() { return normalMap != null; }
    public boolean hasRoughnessMap() {
        return roughnessMap != null;
    }

    public long getTextureHandle() {
        return hasTexture() ? texture.getHandle() : 0L;
    }
    public long getNormalHandle() {
        return hasNormalMap() ? normalMap.getHandle() : 0L;
    }
    public long getRoughnessHandle() {
        return hasRoughnessMap() ? roughnessMap.getHandle() : 0L;
    }

    public void setAmbientColor(RGBColor ambientColor) { this.ambientColor = ambientColor; }
    public void setDiffuseColor(RGBColor diffuseColor) { this.diffuseColor = diffuseColor; }
    public void setSpecularColor(RGBColor specularColor) { this.specularColor = specularColor; }
    public void setEmissiveColor(RGBColor emissiveColor) { this.emissiveColor = emissiveColor; }

    public void setReflectance(float reflectance) { this.reflectance = reflectance; }
    public void setEmissiveStrength(float emissiveStrength) { this.emissiveStrength = emissiveStrength; }
    public void setMetallicFactor(float metallicFactor) {
        this.metallicFactor = metallicFactor;
    }
    public void setRoughnessFactor(float roughnessFactor) {
        this.roughnessFactor = roughnessFactor;
    }

    public void setTexturePath(Resource texturePath) {
        this.texturePath = texturePath;
        //throw new RuntimeException("Texture does not exist: " + texturePath);
    }
    public void setNormalMapPath(Resource normalMapPath) { this.normalMapPath = normalMapPath; }
    public void setRoughnessMapPath(Resource roughnessMapPath) {
        this.roughnessMapPath = roughnessMapPath;
    }

    public void setMaterialIndex(int materialIndex) { this.materialIndex = materialIndex; }

    public void setTexture(Texture texture) { this.texture = texture; }
    public void setNormalMap(Texture normalMap) { this.normalMap = normalMap; }
    public void setRoughnessMap(Texture roughnessMap) {
        this.roughnessMap = roughnessMap;
    }



    public static class MaterialStruct extends Struct {

        public MaterialStruct(StructType structType) {
            super(structType);
        }

        @Override
        public Class<?> getRecord() {
            return MaterialRecord.class;
        }

        public record MaterialRecord(
                Vector4f diffuse,
                Vector4f specular,
                float reflectance,
                float roughnessFactor,
                float metallicFactor
        ){}
    }
}