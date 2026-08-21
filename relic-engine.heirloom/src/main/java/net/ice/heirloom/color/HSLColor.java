package net.ice.heirloom.color;

public record HSLColor(float hue, float saturation, float luminance) {

	public HSLColor lighter() {
		return lighter(10);
	}

	public HSLColor lighter(int percent) {
		return new HSLColor(
				hue,
				saturation,
				luminance + (luminance * percent / 100)
		);
	}

	public HSLColor darker() {
		return darker(10);
	}

	public HSLColor darker(int percent) {
		return new HSLColor(
				hue,
				saturation,
				luminance - (luminance * percent / 100)
		);
	}

	public RGBColor toRGBColor() {
		float H = hue / 360;
		float S = saturation / 100;
		float L = luminance / 100;

		float r;
		float g;
		float b;

		if (S == 0) {
			//grayscale color
			r = g = b = L;
		} else {
			float max = L < 0.5f ? L * (1.0f + S) : L + S - (L * S);
			float min = 2.0f * L - max;

			r = hueToRGB(min, max, H + 1.0f / 3.0f); //plus 120 degrees (360 / 1/3 = 120)
			g = hueToRGB(min, max, H);
			b = hueToRGB(min, max, H - 1.0f / 3.0f); //minus 120 degrees
		}

		r = Math.round(r * 255);
		g = Math.round(g * 255);
		b = Math.round(b * 255);

		return new RGBColor(r, g, b);
	}

	private float hueToRGB(float min, float max, float H) {
		if (!(0.0f < H && H < 1.0f)) {
			H = Math.abs(1 - Math.abs(H));
		}

		if (H < 1.0f / 6.0f) {
			return min + (max - min) * 6.0f * H;
		}

		if (H < 1.0f / 2.0f) {
			return max;
		}

		if (H < 2.0f / 3.0f) {
			return min + (max - min) * (2.0f / 3.0f - H) * 6.0f;
		}

		return min;
	}


}
