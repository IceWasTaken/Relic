package net.ice.relic.engine.opengl.model;

import net.ice.relic.engine.util.ColorUtil;

public class Material {

    private float metallic;
    private float specular;
    private float specularTint;
    private float roughness;
    private float anisotropic;
    private float anisotropicRotation;
    private float sheen;
    private float sheenTint;
    private float clearcoat;
    private float clearcoatRoughness;
    private float IOR;
    private float transmission;
    private float transmissionRoughness;
    private ColorUtil.Color emissionColor;
    private float emissionStrength;
    private float alpha;

    public Material(
            float metallic,
            float specular,
            float specularTint,
            float roughness,
            float anisotropic,
            float anisotropicRotation,
            float sheen,
            float sheenTint,
            float clearcoat,
            float clearcoatRoughness,
            float IOR,
            float transmission,
            float transmissionRoughness,
            ColorUtil.Color emissionColor,
            float emissionStrength,
            float alpha
    ) {
        this.metallic = metallic;
        this.specular = specular;
        this.specularTint = specularTint;
        this.roughness = roughness;
        this.anisotropic = anisotropic;
        this.anisotropicRotation = anisotropicRotation;
        this.sheen = sheen;
        this.sheenTint = sheenTint;
        this.clearcoat = clearcoat;
        this.clearcoatRoughness = clearcoatRoughness;
        this.IOR = IOR;
        this.transmission = transmission;
        this.transmissionRoughness = transmissionRoughness;
        this.emissionColor = emissionColor != null ? emissionColor : new ColorUtil.Color(0.0f, 0.0f, 0.0f);
        this.emissionStrength = emissionStrength;
        this.alpha = alpha;
    }
}