package com.gmail.maxilandia.slideshow;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Image {

	private final File file;
	
	private final BufferedImage img;
	
	void display(BufferedImage screen){
		LOGGER.info(String.format("Displaying '%s' at resolution %sx%s", file.getName(), img.getWidth(), img.getHeight()));
		Graphics2D g2d = screen.createGraphics();
		g2d.setPaint(Color.BLACK);
		g2d.fillRect(0, 0, screen.getWidth(), screen.getHeight());
		int leftPos = img.getWidth() < screen.getWidth() ? Integer.valueOf((int) (screen.getWidth() - img.getWidth())) / 2  : 0;
		int topPos = img.getHeight() < screen.getHeight() ? Integer.valueOf((int) (screen.getHeight() - img.getHeight())) / 2  : 0;
		g2d.drawImage(img, leftPos, topPos, null);
		g2d.dispose();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Image.class);
	
}
