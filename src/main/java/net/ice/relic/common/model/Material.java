package net.ice.relic.common.model;

import net.ice.relic.common.cache.TextureCache;
import net.ice.relic.engine.opengl.model.texture.Texture;
import net.ice.relic.engine.util.ColorUtil;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;

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

    private int materialIndex;
    private float reflectance;
    private float emissiveStrength;

    private ColorUtil.Color ambientColor;
    private ColorUtil.Color diffuseColor;
    private ColorUtil.Color specularColor;
    private ColorUtil.Color emissiveColor;

    private String texturePath;
    private String normalMapPath;
    private String ORMMapPath;
    private String emissiveMapPath;

    private Texture texture;
    private Texture normalMap;
    private Texture ormMap;
    private Texture emissiveMap;

    public Material() {
        this.ambientColor = DEFAULT_COLOR;
        this.diffuseColor = DEFAULT_COLOR;
        this.specularColor = DEFAULT_COLOR;
        this.emissiveColor = DEFAULT_COLOR;
        this.materialIndex = 0;
    }

    public static Material processMaterial(AIMaterial aiMaterial, String directory, TextureCache textureCache) {
        Material material = new Material();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color = AIColor4D.create();
            int result;

            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color);
            if (result == aiReturn_SUCCESS) {
                material.setAmbientColor(new ColorUtil.Color(color.r(), color.g(), color.b(), color.a()).convertFromOpenGLColor());
            }
            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
            if (result == aiReturn_SUCCESS) {
                material.setDiffuseColor(new ColorUtil.Color(color.r(), color.g(), color.b(), color.a()).convertFromOpenGLColor());
            }
            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
            if (result == aiReturn_SUCCESS) {
                material.setSpecularColor(new ColorUtil.Color(color.r(), color.g(), color.b(), color.a()).convertFromOpenGLColor());
            }
            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_EMISSIVE, aiTextureType_NONE, 0, color);
            if (result == aiReturn_SUCCESS) {
                material.setEmissiveColor(new ColorUtil.Color(color.r(), color.g(), color.b(), color.a()).convertFromOpenGLColor());
            }

            float reflectance = 0.0f;
            float[] shininessFactor = new float[]{0.0f};
            int[] pMax = new int[]{1};

            result = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0, shininessFactor, pMax);
            if (result != aiReturn_SUCCESS) {
                reflectance = shininessFactor[0];
            }
            material.setReflectance(reflectance);

            AIString aiTexturePath = AIString.calloc(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();
            if (!texturePath.isEmpty()) {
                material.setTexturePath(directory + File.separator + "textures/" + new File(texturePath).getName());
                Texture texture = textureCache.createTexture(material.getTexturePath());
                material.setDiffuseColor(Material.DEFAULT_COLOR);
                material.setTexture(texture);
            }

            AIString aiNormalMapPath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, aiNormalMapPath, (IntBuffer) null, null, null, null, null, null);
            String normalMapPath = aiNormalMapPath.dataString();
            if (!normalMapPath.isEmpty()) {
                material.setNormalMapPath(directory + File.separator + "textures/" + new File(normalMapPath).getName());
                material.setNormalMap(textureCache.createTexture(material.getNormalMapPath()));
            }
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
    public String getORMMapPath() { return ORMMapPath; }
    public String getEmissiveMapPath() {
        return emissiveMapPath;
    }

    public int getMaterialIndex() { return materialIndex; }

    public Texture getTexture() { return texture; }
    public Texture getNormalMap() { return normalMap; }
    public Texture getOrmMap() { return ormMap; }
    public Texture getEmissiveMap() {
        return emissiveMap;
    }

    public boolean hasTexture() { return texture != null; }
    public boolean hasNormalMap() { return normalMap != null; }
    public boolean hasORMMap() { return ormMap != null; }
    public boolean hasEmissiveMap() { return emissiveMap != null; }

    public long getTextureHandle() {
        return hasTexture() ? texture.getBindlessHandle() : 0L;
    }

    public long getNormalHandle() {
        return hasNormalMap() ? normalMap.getBindlessHandle() : 0L;
    }

    public long getORMHandle() {
        return hasORMMap() ? ormMap.getBindlessHandle() : 0L;
    }

    public long getEmissiveHandle() {
        return hasEmissiveMap() ? emissiveMap.getBindlessHandle() : 0L;
    }

    public void setAmbientColor(ColorUtil.Color ambientColor) { this.ambientColor = ambientColor; }
    public void setDiffuseColor(ColorUtil.Color diffuseColor) { this.diffuseColor = diffuseColor; }
    public void setSpecularColor(ColorUtil.Color specularColor) { this.specularColor = specularColor; }
    public void setEmissiveColor(ColorUtil.Color emissiveColor) { this.emissiveColor = emissiveColor; }

    public void setReflectance(float reflectance) { this.reflectance = reflectance; }
    public void setEmissiveStrength(float emissiveStrength) { this.emissiveStrength = emissiveStrength; }

    public void setTexturePath(String texturePath) { this.texturePath = texturePath; }
    public void setNormalMapPath(String normalMapPath) { this.normalMapPath = normalMapPath; }
    public void setORMMapPath(String ORMMapPath) { this.ORMMapPath = ORMMapPath; }
    public void setEmissiveMapPath(String emissiveMapPath) {
        this.emissiveMapPath = emissiveMapPath;
    }

    public void setMaterialIndex(int materialIndex) { this.materialIndex = materialIndex; }

    public void setTexture(Texture texture) { this.texture = texture; }
    public void setNormalMap(Texture normalMap) { this.normalMap = normalMap; }
    public void setORMMap(Texture ormMap) { this.ormMap = ormMap; }
    public void setEmissiveMap(Texture emissiveMap) {
        this.emissiveMap = emissiveMap;
    }
}
