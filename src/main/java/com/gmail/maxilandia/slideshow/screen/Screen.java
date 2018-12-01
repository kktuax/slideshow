package com.gmail.maxilandia.slideshow.screen;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public interface Screen {

	public int getWidth();
	
	public int getHeight();
	
	public void update(Consumer<BufferedImage> screenConsumer);
	
}
