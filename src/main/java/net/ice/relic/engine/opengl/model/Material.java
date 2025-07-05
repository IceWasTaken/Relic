package net.ice.relic.engine.opengl.model;

import net.ice.relic.engine.opengl.model.texture.Texture;
import net.ice.relic.engine.util.ColorUtil;

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

    // Color and property accessors
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

    // Texture object accessors
    public Texture getTexture() { return texture; }
    public Texture getNormalMap() { return normalMap; }
    public Texture getOrmMap() { return ormMap; }
    public Texture getEmissiveMap() {
        return emissiveMap;
    }

    // Texture availability checks
    public boolean hasTexture() { return texture != null; }
    public boolean hasNormalMap() { return normalMap != null; }
    public boolean hasORMMap() { return ormMap != null; }
    public boolean hasEmissiveMap() { return emissiveMap != null; }

    // Bindless texture handle access
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

    // Setters
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
