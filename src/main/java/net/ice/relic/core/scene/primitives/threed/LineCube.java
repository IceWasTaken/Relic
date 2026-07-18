package net.ice.relic.core.scene.primitives.threed;

import net.ice.relic.core.scene.primitives.Primitive;

public class LineCube implements Primitive {

	@Override
	public float[] getVertices() {
		return new float[] {
				// Front
				-1,-1, 1,
				1,-1, 1,
				1, 1, 1,
				-1, 1, 1,

				// Back
				-1,-1,-1,
				1,-1,-1,
				1, 1,-1,
				-1, 1,-1
		};
	}

	@Override
	public int[] getIndices() {
		return new int[] {
				// Front square
				0,1, 1,2, 2,3, 3,0,

				// Back square
				4,5, 5,6, 6,7, 7,4,

				// Connecting edges
				0,4, 1,5, 2,6, 3,7
		};
	}

}
