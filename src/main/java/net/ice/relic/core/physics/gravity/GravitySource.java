package net.ice.relic.core.physics.gravity;

import org.joml.Vector3f;

public interface GravitySource {

    /**
     * Gets the strength of the gravitational field, in m/s^2
     * (positive values pull toward the source).
     */
    float getStrength();

    /**
     * Gets the radius of the gravitational field.
     */
    float getRadius();

    /**
     * Gets the position of the gravity source in world space.
     * Used to calculate direction and distance.
     */

    Vector3f getPosition();

    /**
     * Determines whether gravity should be applied from this source.
     * This method controls if the gravity source is currently active and affecting other objects.
     *
     * @return true if gravity should be applied, false otherwise
     */
    default boolean shouldApply() {
        return true;
    }

    /**
     * Calculates the gravitational acceleration vector
     * applied to an object at the given position.
     */

    default Vector3f getAcceleration(Vector3f objPos) {
        Vector3f direction  = getPosition().sub(objPos);
        float distanceSquared = direction.lengthSquared();

        if(distanceSquared < 1e-6f) {
            return new Vector3f(0,0,0);
        }

        direction.normalize();
        float accel = getStrength() / distanceSquared;

        return direction.mul(accel);
    }

    /**
     * Get the strength of the gravitational field at a position.
     * @param position - the position to be checked.
     * @return gravitational field strength at that position
     */
    default float getStrengthAt(Vector3f position) {
        Vector3f srcPosition = getPosition();
        float distanceSquared = srcPosition.distanceSquared(position);

        if(distanceSquared == 0) {
            return getStrength();
        }

        if(distanceSquared > getRadius() * getRadius()) {
            return 0f;
        }

        return getStrength() / distanceSquared;
    }







}
