package net.ice.relic.common.scene;

import net.ice.relic.common.scene.light.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Lights {

    private AmbientLight ambientLight;
    private DirLight dirLight;
    private List<PointLight> pointLights;
    private List<SpotLight> spotLights;
    private List<EmissiveLight> emissiveLights;

    public Lights() {
        ambientLight = new AmbientLight();
        pointLights = new ArrayList<>();
        spotLights = new ArrayList<>();
        emissiveLights = new ArrayList<>();
        dirLight = new DirLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0), 1.0f);
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public DirLight getDirLight() {
        return dirLight;
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }

    public List<EmissiveLight> getEmissiveLights() {
        return emissiveLights;
    }
}
