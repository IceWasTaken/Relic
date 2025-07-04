package net.ice.relic.engine.opengl.model;

import net.ice.relic.engine.util.ColorUtil;

public class Material {

    public static final ColorUtil.Color DEFAULT_COLOR = new ColorUtil.Color(0.0f, 0.0f, 0.0f, 1.0f);

    private int materialIndex;

    private float reflectance;
    private float emissiveStrength;

    private String texturePath;
    private String normalMapPath;

    private ColorUtil.Color ambientColor;
    private ColorUtil.Color diffuseColor;
    private ColorUtil.Color specularColor;
    private ColorUtil.Color emissiveColor;

    public Material() {
        ambientColor = DEFAULT_COLOR;
        diffuseColor = DEFAULT_COLOR;
        specularColor = DEFAULT_COLOR;
        emissiveColor = DEFAULT_COLOR;
        materialIndex = 0;
    }

    public ColorUtil.Color getAmbientColor() {
        return ambientColor;
    }

    public ColorUtil.Color getDiffuseColor() {
        return diffuseColor;
    }

    public ColorUtil.Color getSpecularColor() {
        return specularColor;
    }

    public ColorUtil.Color getEmissiveColor() {
        return emissiveColor;
    }

    public float getReflectance() {
        return reflectance;
    }

    public float getEmissiveStrength() {
        return emissiveStrength;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getNormalMapPath() {
        return normalMapPath;
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public void setAmbientColor(ColorUtil.Color ambientColor) {
        this.ambientColor = ambientColor;
    }

    public void setDiffuseColor(ColorUtil.Color diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public void setSpecularColor(ColorUtil.Color specularColor) {
        this.specularColor = specularColor;
    }

    public void setEmissiveColor(ColorUtil.Color emissiveColor) {
        this.emissiveColor = emissiveColor;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public void setNormalMapPath(String normalMapPath) {
        this.normalMapPath = normalMapPath;
    }

    public void setMaterialIndex(int materialIndex) {
        this.materialIndex = materialIndex;
    }

    public void setEmissiveStrength(float emissiveStrength) {
        this.emissiveStrength = emissiveStrength;
    }
}