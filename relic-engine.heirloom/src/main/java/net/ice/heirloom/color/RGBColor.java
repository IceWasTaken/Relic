package net.ice.heirloom.color;

import org.joml.Vector3f;
import org.joml.Vector4f;

public record RGBColor(float red, float green, float blue, float alpha) {
	public RGBColor(float red, float green, float blue, float alpha) {
		this.red = Math.clamp(red, 0, 255);
		this.green = Math.clamp(green, 0, 255);
		this.blue = Math.clamp(blue, 0, 255);
		this.alpha = Math.clamp(alpha, 0, 255);
	}

	public RGBColor(float red, float green, float blue) {
		this(red, green, blue, 255);
	}

	public RGBColor lighter() {
		return lighter(10);
	}

	public RGBColor lighter(int percent) {
		return toHSLColor().lighter(10).toRGBColor();
	}

	public RGBColor darker() {
		return darker(10);
	}

	public RGBColor darker(int percent) {
		return toHSLColor().lighter(percent).toRGBColor();
	}

	/// [Based on](https://www.niwa.nu/2013/05/math-behind-colorspace-conversions-rgb-hsl/)
	public HSLColor toHSLColor() {
		RGBColor adjRangeColor = this.div();

		float adjR = adjRangeColor.red;
		float adjG = adjRangeColor.green;
		float adjB = adjRangeColor.blue;

		float min = adjRangeColor.min();
		float max = adjRangeColor.max();
		float delta = max - min;

		float H = 0.0f;
		float S = 0.0f;
		float L = (min + max) / 2;

		if (delta != 0.0f) {
			S = delta / (1.0f - Math.abs(2.0f * L - 1.0f));

			if (max == adjR) {
				H = ((adjG - adjB) / delta);
			} else if (max == adjG) {
				H = ((adjB - adjR) / delta) + 2.0f;
			} else if (max == adjB) {
				H = ((adjR - adjG) / delta) + 4.0f;
			}
		}

		H *= 60.0f;
		S *= 100;
		L *= 100;

		if (H < 0) {
			H += 360;
		}

		return new HSLColor(H, S, L);
	}

	public RGBColor invert() {
		return new RGBColor(255 - red, 255 - green, 255 - blue);
	}

	public RGBColor mult() {
		return new RGBColor(red * 255, green * 255, blue * 255, alpha * 255);
	}

	public RGBColor div() {
		return new RGBColor(red / 255, green / 255, blue / 255, alpha / 255);
	}

	public RGBColor fromVec3f(Vector3f vec) {
		return new RGBColor(vec.x, vec.y, vec.z, 1f);
	}

	public RGBColor fromVec4f(Vector4f vec) {
		return new RGBColor(vec.x, vec.y, vec.z, vec.z);
	}

	public Vector3f vec3f() {
		return new Vector3f(red, green, blue);
	}

	public Vector4f vec4f() {
		return new Vector4f(red, green, blue, alpha);
	}

	public float max() {
		return Math.max(Math.max(red, green), blue);
	}

	public float min() {
		return Math.min(Math.min(red, green), blue);
	}


}
