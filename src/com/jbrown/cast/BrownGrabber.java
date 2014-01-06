package com.jbrown.cast;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.Arrays;

public class BrownGrabber extends PixelGrabber {
	private BufferedImage _image;

	public BrownGrabber(BufferedImage image, int x, int y, int w, int h,
			boolean forceRGB) {
		super(image, x, y, w, h, forceRGB);
		_image = image;
	}

	public BufferedImage getImage() {
		return _image;
	}

	public boolean isPixcelsMatch(BrownGrabber newPixels) {
		if (newPixels == null) {
			return false;
		}

		try {
			int[] data1 = null;
			if (this.grabPixels()) {
				int width = this.getWidth();
				int height = this.getHeight();
				data1 = new int[width * height];
				data1 = (int[]) this.getPixels();
				Arrays.sort(data1);
			}

			int[] data2 = null;
			if (newPixels.grabPixels()) {
				int width = newPixels.getWidth();
				int height = newPixels.getHeight();
				data2 = new int[width * height];
				data2 = (int[]) newPixels.getPixels();
				Arrays.sort(data2);
			}

			return java.util.Arrays.equals(data1, data2);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		return false;
	}
}
