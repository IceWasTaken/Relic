package net.ice.heirloom.color;

public enum Colors {
	RED(232, 13, 13),
	ORANGE(255, 119, 0),
	YELLOW(255, 218, 10),
	GREEN(16, 173, 55),
	LIME(0, 255, 64),
	BLUE(10, 27, 255),
	LIGHT_BLUE(10, 206, 255),
	PURPLE(147, 13, 224),
	BLACK(0, 0, 0),
	LIGHT_GRAY(196, 196, 196),
	GRAY(64, 64, 64),
	DARK_GRAY(36, 36, 36),
	WHITE(255, 255, 255);

	private final HSLColor HSLColor;
	private final RGBColor RGBColor;

	Colors(float red, float green, float blue) {
		this.RGBColor = new RGBColor(red, green, blue);
		this.HSLColor = RGBColor.toHSLColor();
	}

	public RGBColor getRGBColor() {
		return RGBColor;
	}

	public HSLColor getHSLColor() {
		return HSLColor;
	}
}
