package AfkKiller;
import java.awt.image.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

//https://docs.oracle.com/javase/8/docs/api/java/awt/Robot.html 

public class Test {
	boolean deleteThisVarLater = false;
	Rectangle screen;
	public static void writeImageFile (BufferedImage photo,String filename) throws FileNotFoundException {
    	try {
    		File file = new File(filename);
    		ImageIO.write(photo,"png",file);
    	} catch (IOException e) {
    		throw new FileNotFoundException();
    	}
    }
	public static BufferedImage getScreenshot() {
		try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            return screenshot;
        } catch (AWTException e) {
            e.printStackTrace();
            return null;
        }
	}
	// COMPARING COLORS ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■v
	public static int[] splitFourBytes (int input) {
		int alpha = (input >> 24) & 0xFF;
		int red   = (input >> 16) & 0xFF;
		int green = (input >> 8)  & 0xFF;
		int blue  = (input)       & 0xFF;
		return new int[] {alpha, red, green, blue};
	}
	public static double colorDist(int color1, int color2) {
		int[] one = splitFourBytes(color1);
		int[] two = splitFourBytes(color2);
		return Math.sqrt(Math.pow(one[0] - two[0],2) + // Red
				         Math.pow(one[1] - two[1],2) + // Green
				         Math.pow(one[2] - two[2],2)); // Blue
	}
	public static boolean colorCompare(int color1, int color2, double radius) {
		//System.out.println((colorDist(color1,color2) <= radius));
		return colorDist(color1,color2) <= radius;
	}
	// DETECTING COLORS ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	public static BufferedImage whiteBleeding(BufferedImage stencil) {
		BufferedImage output = new BufferedImage(stencil.getWidth(),stencil.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < stencil.getHeight();i++) {
			for (int j = 0; j < stencil.getWidth(); j++) {
				if (splitFourBytes(stencil.getRGB(j, i))[1] >= 240 && 
						splitFourBytes(stencil.getRGB(j, i))[2] >= 240 && 
						splitFourBytes(stencil.getRGB(j, i))[3] >= 240) {
					output.setRGB(j, i, 0xFFFF0000);
				}
			}
		}
		return output;
	}
	public static BufferedImage colorBleeding(BufferedImage stencil, int color, int radius) {
		BufferedImage output = new BufferedImage(stencil.getWidth(),stencil.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < stencil.getHeight();i++) {
			for (int j = 0; j < stencil.getWidth(); j++) {
				if (colorCompare(stencil.getRGB(j, i),color,radius)) {
					output.setRGB(j, i, 0xFFFF0000);
				}
			}
		}
		return output;
	}
	// COUNTING BLOODPOINT DIGITS ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	public static BufferedImage digitImageMap() {
		BufferedImage output = whiteBleeding(getScreenshot());
		// Crop
		for (int i = 0; i < output.getHeight(); i++) {
			for (int j = 0; j < output.getWidth(); j++) {
				if (!(i < (output.getHeight()/2) && j > (output.getWidth()/2))) {
					output.setRGB(j, i, 0xFF0000FF);
				}
			}
		}
		// Fill Collumns
		for (int j = 0; j < output.getWidth(); j++) {
			boolean red = false;
			for (int i = 0; i < output.getHeight(); i++) {
				if (output.getRGB(j, i) == 0xFFFF0000)
					red = true;
			}
			if (red) {
				for (int i = 0; i < output.getHeight(); i++) {
					output.setRGB(j, i, 0xFFFF0000);
				}
			}
		}
		return output;
	}
	public static int getBloodpointDigits() {
		BufferedImage img = digitImageMap();
		System.out.println(img.getWidth());
		int width = Math.round(img.getWidth()/2);
		boolean[] coll = new boolean[width];
		System.out.println(width);
		for (int i = (img.getWidth() - 1); i > (width + 1); i--) {
			//System.out.println(i + ", " + (img.getWidth() - i));
			if(img.getRGB(i, 10) == 0xFFFF0000) {
				coll[(img.getWidth() - i)] = true;
			}
		}
		int firstRed = 0;
		int firstBlack = 0;
		// These find the first instances of black and red columns //TODO Rewrite these to get rid of that stupid coll variable
		for (int i = img.getWidth() - 1; i > width; i--) { if(coll[(img.getWidth() - i)]) { firstRed = i; break; }}
		for (int i = firstRed; i > width; i--) { if(!coll[(img.getWidth() - i)]) { firstBlack = i; break; }}
		for (boolean i : coll) System.out.println("DEBUG: " + i);
		int difference = firstRed - firstBlack; // The threshold by which the end of the blood points will be determined
		System.out.println(difference);
		// Three Variables For The Loop To Play With
		int temp = difference * 2; int previous = 0xFF000000; int digits = 0;
		for (int i = firstRed; i > width; i--) {
			System.out.println(i);
			int x = img.getRGB(i, 55);
			if (x == 0xFFFF0000) {
				if (!(previous == x))
					digits++;
				temp = difference * 2;
			} else {
				temp--;
			}
			previous = x;
			if (temp <= 0) { return digits; }
		}
		return -1;
	}
	public static void main(String[] bannanas) throws Exception {
		if (new Test().deleteThisVarLater) {
			writeImageFile(getScreenshot(),"testScreenshot.png");
			writeImageFile(whiteBleeding(getScreenshot()),"whiteBleeding.png");
			writeImageFile(colorBleeding(getScreenshot(),0xFFFF00FF,5),"colorBleeding.png");
		}
		System.out.println("Timer Flag");
		AfkKiller.sleep(5);
		writeImageFile(whiteBleeding(getScreenshot()),"whiteBleeding2.png");
		writeImageFile(digitImageMap(),"digitImageMap.png");
		System.out.println("The Number Of Bloodpoint Digits Is " + getBloodpointDigits());
	}
}
