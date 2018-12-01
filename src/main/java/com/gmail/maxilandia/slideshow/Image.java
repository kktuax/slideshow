package com.gmail.maxilandia.slideshow;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Image implements Consumer<BufferedImage> {

	private final File file;
	
	private final BufferedImage img;
	
	@Override
	public void accept(BufferedImage screen) {
		LOGGER.info(String.format("Displaying '%s' at resolution %sx%s", file.getName(), img.getWidth(), img.getHeight()));
		Graphics2D g2d = screen.createGraphics();
		g2d.setPaint(Color.BLACK);
		g2d.fillRect(0, 0, screen.getWidth(), screen.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		int leftPos = img.getWidth() < screen.getWidth() ? Integer.valueOf((int) (screen.getWidth() - img.getWidth())) / 2  : 0;
		if(leftPos > 0){
			g2d.setPaint(Color.WHITE);
			g2d.setFont(FONT.deriveFont(rotate(-90)));
	        String folderName = file.getParentFile().getName();
	        g2d.drawString(folderName, 20, screen.getHeight() - 20);
	        g2d.setFont(FONT.deriveFont(rotate(90)));
	        String fileName = file.getName();
	        g2d.drawString(fileName, screen.getWidth() - 20, 20);
		}
		int topPos = img.getHeight() < screen.getHeight() ? Integer.valueOf((int) (screen.getHeight() - img.getHeight())) / 2  : 0;
		g2d.drawImage(img, leftPos, topPos, null);
		g2d.dispose();
	}
	
	private static AffineTransform rotate(int degrees){
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(degrees), 0, 0);
		return at;
	}
	
	private static final Font FONT = new Font("Arial", Font.BOLD, 20);
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Image.class);
	
}
