package net.ice.relic.core.physics;

import net.ice.relic.common.util.AssimpUtil;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIAABB;
import org.lwjgl.assimp.Assimp;

public class AABB {

	//origin
	private Vector3f posStart;

	//size
	private Vector3f size;

	//origin + size
	private Vector3f posEnd;

	public AABB(Vector3f posStart, Vector3f size) {
		this.posStart = posStart;
		this.size = size;
		this.posEnd = new Vector3f(posStart).add(size);
	}

	public static AABB fromAIAABB(AIAABB aiAABB) {
		return new AABB(
				AssimpUtil.aiVec3ToVec3(aiAABB.mMin()),
				minMaxToSize(AssimpUtil.aiVec3ToVec3(aiAABB.mMin()), AssimpUtil.aiVec3ToVec3(aiAABB.mMax()))
		);
	}

	public boolean collides(AABB other) {
		boolean overlapX = (posStart.x() < other.posEnd.x() && posEnd.x() > other.posStart.x());
		boolean overlapY = (posStart.y() < other.posEnd.y() && posEnd.y() > other.posStart.y());
		boolean overlapZ = (posStart.z() < other.posEnd.z() && posEnd.z() > other.posStart.z());
		return overlapX || overlapY || overlapZ;
	}

	private static Vector3f minMaxToSize(Vector3f min, Vector3f max) {
		return new Vector3f(max).sub(min);
	}

	public Vector3f getSize() {
		return size;
	}

	public Vector3f getHalfSize() {
		return size.mul(0.5f, new Vector3f());
	}

	public Vector3f getCenter() {
		return (new Vector3f(posStart).add(new Vector3f(posEnd))).mul(0.5f);
	}

	public Vector3f getPosStart() {
		return posStart;
	}

	public Vector3f getPosEnd() {
		return posEnd;
	}
}
