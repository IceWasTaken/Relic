package net.ice.relic.core.rendering;

import net.ice.heirloom.color.RGBColor;
import net.ice.relic.core.rendering.backend.opengl.renderers.GLDebugRenderer;
import org.joml.Vector3f;

///Allows class to be a target of {@link GLDebugRenderer GLVisualizeRendeer}
public interface DebugRenderable {

	RGBColor getDebugColor();
	Vector3f getDebugPos();
	Vector3f getDebugScale();

}
