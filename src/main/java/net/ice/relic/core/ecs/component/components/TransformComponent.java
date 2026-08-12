package net.ice.relic.core.ecs.component.components;

import net.ice.relic.core.ecs.component.Component;
import net.ice.relic.core.ecs.component.Editable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TransformComponent extends Component {

	public static final List<TransformComponent> COMPONENTS = new ArrayList<>();

	@Editable
	private Vector3f position = new Vector3f(0, 0, 0);

	@Editable
	private Vector3f scale = new Vector3f(1.0f);

	@Editable
	private Quaternionf rotation = new Quaternionf();

	private Matrix4f transformationMatrix = new Matrix4f();

	public TransformComponent() {
		COMPONENTS.add(this);
	}

	@Override
	public void update() {
		transformationMatrix
				.identity()
				.translate(position)
				.rotate(rotation)
				.scale(scale);
	}

	public TransformComponent setPosition(float x, float y, float z) {
		position.set(x, y, z);
		update();
		return this;
	}
	public TransformComponent addPosition(float x, float y, float z) {
		position.add(x, y, z);
		update();
		return this;
	}
	public Vector3f getPosition() {
		return position;
	}

	public TransformComponent setRotation(float x, float y, float z, float w) {
		rotation.set(x, y, z, w);
		update();
		return this;
	}
	public TransformComponent addRotation(float x, float y, float z, float w) {
		rotation.add(x, y, z, w);
		update();
		return this;
	}
	public Quaternionf getRotation() {
		return rotation;
	}

	public TransformComponent setScale(float x, float y, float z) {
		scale.set(x, y, z);
		update();
		return this;
	}
	public TransformComponent addScale(float x, float y, float z) {
		scale.add(x, y, z);
		update();
		return this;
	}
	public Vector3f getScale() {
		return scale;
	}

	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;
	}
}
