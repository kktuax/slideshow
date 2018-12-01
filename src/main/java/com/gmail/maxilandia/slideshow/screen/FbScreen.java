package com.gmail.maxilandia.slideshow.screen;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tw.pi.NarSystem;
import org.tw.pi.framebuffer.FrameBuffer;

public class FbScreen implements Screen, AutoCloseable {

	private final FrameBuffer frameBuffer;
	
	private final BufferedImage screen;
	
	public FbScreen(String fbLocation){
		super();
		NarSystem.loadLibrary();
		this.frameBuffer = new FrameBuffer(fbLocation);
		this.screen = frameBuffer.getScreen();
		LOGGER.info(String.format("Initialized frameBuffer %s with resolution of %sx%s", fbLocation, getWidth(), getHeight()));
	}
	
	@Override
	public int getWidth() {
		return screen.getWidth();
	}

	@Override
	public int getHeight() {
		return screen.getHeight();
	}

	@Override
	public void update(Consumer<BufferedImage> screenConsumer) {
		screenConsumer.accept(screen);
	}

	@Override
	public void close() throws Exception {
		frameBuffer.close();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(FbScreen.class);	
	
}
