package net.ice.relic.engine.opengl.scene;

import net.ice.relic.engine.opengl.Uniforms;
import net.ice.relic.engine.opengl.model.Model;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SceneObject {

    private final String id;
    private final Model model;
    private Matrix4f modelMatrix;
    private Vector3f position;
    private Quaternionf rotation;
    private float scaleFactor;

    public SceneObject(String id, Model model) {
        this.id = id;
        this.model = model;
        modelMatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scaleFactor = 1f;
    }

    public void render(Uniforms uniforms) {
        uniforms.setUniform("model", getModelMatrix());
        //model.render(uniforms);
    }


    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public void setRotation(float x, float y, float z, float angle) {
        this.rotation.fromAxisAngleDeg(x, y, z, angle);
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public Matrix4f getModelMatrix() {
        modelMatrix.identity()
                .translate(position)
                .rotate(rotation)
                .scale(scaleFactor);
        return modelMatrix;
    }

    public String getId() {
        return id;
    }

    public Model getModel() {
        return model;
    }
}
