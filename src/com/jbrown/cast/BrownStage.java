package com.jbrown.cast;
 

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.plaf.DimensionUIResource;

import org.apache.commons.codec.binary.Base64;

public class BrownStage extends JPanel {
	Dimension _originalDimenetion;
	Dimension _visibleDimenetion;
	BufferedImage _visibleImage;
	BrownGrabber _currentPixels;// Lazy load
	BrownGrabber _newPixels;
	ExecutorService _threadExecutor;
	boolean _isEventOccured;
	BrownActorI _actor;
	boolean _isBroadcastOn;

	public BrownStage(BrownActorI actor) {
		try {
			_originalDimenetion = new DimensionUIResource(this.getWidth(),
					this.getHeight());
			_visibleDimenetion = new DimensionUIResource(this.getWidth(),
					this.getHeight());
			_threadExecutor = Executors.newFixedThreadPool(1);
			_actor = actor;
			_isBroadcastOn = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		BufferedImage image = record();
		if (image != null) {
			_originalDimenetion = new DimensionUIResource(image.getWidth(),
					image.getHeight());
			_visibleDimenetion = new DimensionUIResource(this.getWidth(),
					this.getHeight());

			Image img = image.getScaledInstance(
					(int) _visibleDimenetion.getWidth(),
					(int) _visibleDimenetion.getHeight(), Image.SCALE_SMOOTH);

			g.drawImage(img, 0, 0, this);
		}
	}

	public BufferedImage record() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		_newPixels = _actor.record(new BrownSpot(new Rectangle(screenSize)));

		boolean isPixcelMatched = _newPixels.isPixcelsMatch(_currentPixels);

		if (isPixcelMatched) {
			// System.out.println("NO Pixel change noticed+" + new Date());
		} else {
			System.out.println("Pixel change noticed!!!");
			_visibleImage = _newPixels.getImage();
			_currentPixels = _newPixels;
			if (_isBroadcastOn) {
				post();
			}
		}

		return _newPixels.getImage();
	}

	public void setBroadcastOn(boolean flag) {
		_isBroadcastOn = flag;
	}

	public synchronized boolean isBroadcastOn() {
		return _isBroadcastOn;
	}

	private void post() {
		Callable<String> callable = new Poster(
				"http://happy.javabrown.com/user/services/pipe/push.php",
				base64Image(), _actor.getIpAddress());
		try {
			Future<String> future = _threadExecutor.submit(callable);
			System.out.println(new Date() + ": " + future.get());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			new BrownShooter()
					.get("http://happy.javabrown.com/user/services/pipe/push.php?unlink="
							+ _actor.getIpAddress());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Disconnected");
	}

	public String base64Image() {
		return encodeToString(_visibleImage, null);
	}

	public String encodeToString(BufferedImage image, String imageType) {
		try {

			if (imageType == null || imageType.trim().length() == 0) {
				imageType = "jpg";
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, imageType, baos);
			baos.flush();
			Base64 base = new Base64(false);
			String encodedImage = base.encodeToString(baos.toByteArray());
			baos.close();
			encodedImage = java.net.URLEncoder.encode(encodedImage,
					"ISO-8859-1");
			return encodedImage;
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	// boolean isPixcelsMatch(PixelGrabber newPixels, PixelGrabber
	// currentPixels){
	// PixelGrabber grab1 = newPixels;
	// PixelGrabber grab2 = currentPixels;
	//
	// if (grab1 == null || grab2 == null) {
	// return false;
	// }
	//
	// try {
	// int[] data1 = null;
	//
	// if (grab1.grabPixels()) {
	// int width = grab1.getWidth();
	// int height = grab1.getHeight();
	// data1 = new int[width * height];
	// data1 = (int[]) grab1.getPixels();
	// Arrays.sort(data1);
	// }
	//
	// int[] data2 = null;
	//
	// if (grab2.grabPixels()) {
	// int width = grab2.getWidth();
	// int height = grab2.getHeight();
	// data2 = new int[width * height];
	// data2 = (int[]) grab2.getPixels();
	// Arrays.sort(data2);
	// }
	//
	// boolean isPixcelMatched = java.util.Arrays.equals(data1, data2);
	//
	// //if (!isPixcelMatched) {
	// // _currentPixels = _newPixels;
	// //}
	//
	// return isPixcelMatched;
	//
	// } catch (InterruptedException ex) {
	// ex.printStackTrace();
	// }
	//
	// return false;
	// }

	private void registerEvents() {
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				_isEventOccured = true;
				System.out.println("mouse");
			}
		});

		this.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				_isEventOccured = true;
				System.out.println("key");
			}
		});
	}
}
