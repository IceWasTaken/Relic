package net.ice.heirloom.color;

public class ColorTest {

	public static void main(String[] args) {
		RGBColor rgbColor = new RGBColor(70, 104, 176);
		HSLColor hslColor = rgbColor.toHSLColor();
		System.out.println("INPUT COLOR:");
		System.out.println(rgbColor.getRed());
		System.out.println(rgbColor.getGreen());
		System.out.println(rgbColor.getBlue());
		System.out.println("OUTPUT HSL:");
		System.out.println(hslColor.getHue());
		System.out.println(hslColor.getSaturation());
		System.out.println(hslColor.getLuminance());
		RGBColor convertedRGB = hslColor.toRGBColor();
		System.out.println("OUTPUT COLOR:");
		System.out.println(convertedRGB.getRed());
		System.out.println(convertedRGB.getGreen());
		System.out.println(convertedRGB.getBlue());


	}
}
