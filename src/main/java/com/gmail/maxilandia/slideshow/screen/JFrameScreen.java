package com.gmail.maxilandia.slideshow.screen;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class JFrameScreen implements Screen {

	private final JFrame frame;
	
	private final BufferedImage screen;
	
	public JFrameScreen(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		this.frame = new JFrame();
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.screen = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		ImageIcon icon = new ImageIcon(screen);
		frame.getContentPane().add(new JLabel(icon));
	}

	private final int width, height;
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void update(Consumer<BufferedImage> screenConsumer) {
		screenConsumer.accept(screen);
		frame.revalidate();
		frame.repaint();
	}

}
