package net.ice.relic.core.model.material;

import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.opengl.object.resource.GLTexture;
import net.ice.heirloom.color.RGBColor;
import net.ice.heirloom.io.resource.Resource;
import net.ice.relic.core.cache.TextureCache;
import org.joml.Vector4f;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AITexture;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static net.ice.relic.common.util.AssimpUtil.getMaterialColor;
import static org.lwjgl.assimp.Assimp.*;

public class Material {

    public static final RGBColor DEFAULT_COLOR = new RGBColor(0.0f, 0.0f, 0.0f, 1.0f);

    private final Map<MaterialColor, RGBColor> materialColors = new HashMap<>();
    private final Map<MaterialFactor, Float> materialFactors = new HashMap<>();

    private int materialIndex;

    private Texture albedoMap;
    private Texture normalMap;
    private Texture roughnessMap;

    public Material() {
        this.materialIndex = 0;
    }

    public RGBColor getColor(MaterialColor color) {
        return materialColors.getOrDefault(color, DEFAULT_COLOR);
    }

    public float getFactor(MaterialFactor factor) {
        return materialFactors.getOrDefault(factor, 0f);
    }

    public static Material processMaterial(AIMaterial aiMaterial, Resource resource, TextureCache textureCache) {
        Material material = new Material();
        Map<MaterialColor, RGBColor> materialColors = material.getMaterialColors();
        Map<MaterialFactor, Float> materialFactors = material.getMaterialFactors();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            materialColors.put(MaterialColor.DIFFUSE, getMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE).mult());
            materialColors.put(MaterialColor.AMBIENT, getMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT).mult());
            materialColors.put(MaterialColor.SPECULAR, getMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR).mult());
            materialColors.put(MaterialColor.EMISSIVE, getMaterialColor(aiMaterial, AI_MATKEY_COLOR_EMISSIVE).mult());
            materialColors.put(MaterialColor.TRANSPARENT, getMaterialColor(aiMaterial, AI_MATKEY_COLOR_TRANSPARENT).mult());
            materialColors.put(MaterialColor.REFLECTIVE, getMaterialColor(aiMaterial, AI_MATKEY_COLOR_REFLECTIVE).mult());

            materialFactors.put(MaterialFactor.METALLIC_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_METALLIC_FACTOR));
            materialFactors.put(MaterialFactor.ROUGHNESS_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_ROUGHNESS_FACTOR));
            materialFactors.put(MaterialFactor.ANISOTROPY_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_ANISOTROPY_FACTOR));
            materialFactors.put(MaterialFactor.SPECULAR_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_SPECULAR_FACTOR));
            materialFactors.put(MaterialFactor.GLOSSINESS_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_GLOSSINESS_FACTOR));

            materialFactors.put(MaterialFactor.SHEEN_COLOR_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_SHEEN_COLOR_FACTOR));
            materialFactors.put(MaterialFactor.SHEEN_ROUGHNESS_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_SHEEN_ROUGHNESS_FACTOR));

            materialFactors.put(MaterialFactor.CLEARCOAT_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_CLEARCOAT_FACTOR));
            materialFactors.put(MaterialFactor.CLEARCOAT_ROUGHNESS_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_CLEARCOAT_ROUGHNESS_FACTOR));

            materialFactors.put(MaterialFactor.TRANSMISSION_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_TRANSMISSION_FACTOR));
            materialFactors.put(MaterialFactor.VOLUME_THICKNESS_FACTOR, getFloatArray(aiMaterial, AI_MATKEY_VOLUME_THICKNESS_FACTOR));

            materialFactors.put(MaterialFactor.REFLECTANCE, getFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH));

            material.albedoMap = getTextureMap(stack, aiMaterial, textureCache, resource, aiTextureType_DIFFUSE);
            material.normalMap = getTextureMap(stack, aiMaterial, textureCache, resource, aiTextureType_NORMALS);
            material.roughnessMap = getTextureMap(stack, aiMaterial, textureCache, resource, aiTextureType_GLTF_METALLIC_ROUGHNESS);

            return material;
        }
    }

    private static Texture getTextureMap(MemoryStack stack, AIMaterial aiMaterial, TextureCache textureCache, Resource resource, int mapType) {
        AIString aiTexturePath = AIString.calloc(stack);
        aiGetMaterialTexture(aiMaterial, mapType, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
        String texturePath = aiTexturePath.dataString();
        if(!texturePath.isEmpty()) {
            return textureCache.createTexture(Resource.getResource(resource.getNamespace(), "assets/textures/" + new File(texturePath).getName()));
        }
        return null;
    }

    private Map<MaterialColor, RGBColor> getMaterialColors() {
        return materialColors;
    }

    private Map<MaterialFactor, Float> getMaterialFactors() {
        return materialFactors;
    }


    private static float getFloatArray(AIMaterial aiMaterial, String type) {
        float[] returns = new float[]{0.0f};
        if (aiGetMaterialFloatArray(aiMaterial, type, aiTextureType_NONE, 0, returns, new int[]{1}) != aiReturn_SUCCESS) {
            return returns[0];
        }
        return returns[0];
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public void setMaterialIndex(int materialIndex) {
        this.materialIndex = materialIndex;
    }

    public long getTextureHandle() {
        return albedoMap != null ? albedoMap.getHandle() : 0L;
    }
    public long getNormalHandle() {
        return normalMap != null ? normalMap.getHandle() : 0L;
    }
    public long getRoughnessHandle() {
        return roughnessMap != null ? roughnessMap.getHandle() : 0L;
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