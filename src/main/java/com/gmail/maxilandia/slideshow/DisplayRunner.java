package com.gmail.maxilandia.slideshow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tw.pi.NarSystem;
import org.tw.pi.framebuffer.FrameBuffer;

@Component
public class DisplayRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		Random rand = new Random();
		File[] files = folder.listFiles(f -> f.getName().toLowerCase().endsWith(".jpg"));
		LOGGER.info(String.format("Found %s images in %s", files.length, folder));
		NarSystem.loadLibrary();
		FrameBuffer frameBuffer = new FrameBuffer(fbLocation);
		try{
			BufferedImage screen = frameBuffer.getScreen();
			Dimension boundary = new Dimension(screen.getWidth(), screen.getHeight());
			LOGGER.info(String.format("Found resolution of %sx%s", screen.getWidth(), screen.getHeight()));
			while (true) {
				File file = files[rand.nextInt(files.length)];
				BufferedImage img = scaleImage(ImageLoader.load(file), boundary);
				LOGGER.info(String.format("Displaying '%s' at resolution %sx%s for %s seconds", file.getName(), img.getWidth(), img.getHeight(), duration));
				Graphics2D g2d = screen.createGraphics();
				g2d.setPaint(Color.BLACK);
				g2d.fillRect(0, 0, screen.getWidth(), screen.getHeight());
				int leftPos = img.getWidth() < boundary.getWidth() ? Integer.valueOf((int) (boundary.getWidth() - img.getWidth())) / 2  : 0;
				int topPos = img.getHeight() < boundary.getHeight() ? Integer.valueOf((int) (boundary.getHeight() - img.getHeight())) / 2  : 0;
				g2d.drawImage(img, leftPos, topPos, null);
				g2d.dispose();
				Thread.sleep(1000 * duration);
			}
		}finally{
			frameBuffer.close();
		}
	}

	private static BufferedImage scaleImage(BufferedImage oImg, Dimension boundary) {
		Dimension imgSize = new Dimension(oImg.getWidth(), oImg.getHeight());
		int original_width = imgSize.width, original_height = imgSize.height;
		int new_width = original_width, new_height = original_height;
		if (original_width > boundary.width) {
			new_width = boundary.width; // scale width to fit
			new_height = (new_width * original_height) / original_width; // scale height to maintain aspect ratio
		}
		if (new_height > boundary.height) {
			new_height = boundary.height; // scale height to fit instead
			new_width = (new_height * original_width) / original_height; // scale width to maintain aspect ratio
		}
		return Scalr.resize(oImg, new_width, new_height);
	}
	
	@Value("${folder}")
	private File folder;
	
	@Value("${duration}")
	private Integer duration;
	
	@Value("${frame-buffer-location}")
	private String fbLocation;

	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayRunner.class);

}
