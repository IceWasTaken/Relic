package net.ice.relic.engine.opengl.scene;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.engine.opengl.AnimationData;
import net.ice.relic.engine.opengl.model.Model;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Rewrite(reason = "want to change to allow for child/parent configurations and for empty objects")
public class SceneObject {

    private final String id;
    private final Model model;

    private AnimationData animationData;
    private Matrix4f modelMatrix;
    private Vector3f position;
    private Quaternionf rotation;

    private float scale;

    public SceneObject(String id, Model model) {
        this.id = id;
        this.model = model;
        modelMatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = 1f;
    }

    public void update() {
        modelMatrix.translationRotateScale(position, rotation, scale);
    }

    //Setters/Getters

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public void setRotation(float x, float y, float z, float w) {
        this.rotation.fromAxisAngleDeg(x, y, z, w);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }



//    public void setPosition(float x, float y, float z) {
//        this.position.x = x;
//        this.position.y = y;
//        this.position.z = z;
//    }

    public SceneObject setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
        return this;
    }

    public AnimationData getAnimationData() {
        return animationData;
    }

    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scale = scaleFactor;
    }

    public String getId() {
        return id;
    }

    public Model getModel() {
        return model;
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }
}
