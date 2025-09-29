package net.ice.relic.core.component.physics;

import net.ice.relic.core.component.Component;
import net.ice.relic.core.physics.gravity.GravitySource;
import org.joml.Vector3f;

public class GravitySourceComponent extends Component implements GravitySource {

    private float strength = 1f;
    private float radius = Float.MAX_VALUE;

    public GravitySourceComponent() {

    }

    public GravitySourceComponent(float strength, float radius) {
        this.radius = radius;
        this.strength = strength;
    }

    public void setStrength(float strength) {
        this.strength = Math.max(0f, strength);
    }

    public void setRadius(float radius) {
        this.radius = Math.max(0f, radius);
    }

    @Override
    public float getStrength() {
        return strength;
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public Vector3f getPosition() {
        return attachedObject.getTransform().getPosition();
    }

    @Override
    public String toString() {
        return "--Gravity Source--" +
                "\n[strength = " + strength + "]" +
                "\n[radius = " + radius + "]" +
                "\n[position = " + getPosition() + "]";
    }
}
