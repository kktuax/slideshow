package com.gmail.maxilandia.slideshow.screen;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class JFrameScreen implements Screen {

	private final JFrame frame;
	
	private JLabel label;
	
	private BufferedImage screen;
	
	public JFrameScreen(int width, int height) {
		super();
		this.frame = new JFrame();
		this.frame.setSize(width, height);
		this.frame.setVisible(true);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addLabel();
		this.frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				addLabel();
				frame.revalidate();
				frame.repaint();
			}
		});
	}
	
	private void addLabel(){
		if(label != null){
			frame.getContentPane().remove(label);
		}
		screen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		label = new JLabel(new ImageIcon(screen));
		frame.getContentPane().add(label);
	}
	
	@Override
	public int getWidth() {
		return frame.getWidth();
	}

	@Override
	public int getHeight() {
		return frame.getHeight();
	}
	
	@Override
	public void update(Consumer<BufferedImage> screenConsumer) {
		screenConsumer.accept(screen);
		frame.revalidate();
		frame.repaint();
	}

}
