package net.ice.relic.core.particle;

import net.ice.heirloom.color.RGBColor;
import org.joml.Vector2f;
import org.joml.Vector3f;

//todo: move to SoA or AoS system with off heap memory
//valhalla for the love of all that is good just please come out i'm desperate
public class Particle{

	private Vector3f position;
	private Vector3f velocity;
	private Vector2f scale;
	private RGBColor color;
	private float life;

	public Particle(Vector3f position, Vector3f velocity, RGBColor color, float life) {
		this.position = position;
		this.velocity = velocity;
		this.color = color;
		this.life = life;
	}

	public void update(float deltaTime) {
		life -= deltaTime;

		if(life > 0.0f) {
			velocity.y -= 20f * deltaTime; //gravity
			position.add(new Vector3f(velocity).mul(deltaTime));
			color = color.darker();
		}
	}

	public float getLife() {
		return life;
	}

	public RGBColor getColor() {
		return color;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	@Override
	public String toString() {
		return "particle { position = " + position + ", color = " + color + ", life = " + life + " }";
	}
}
