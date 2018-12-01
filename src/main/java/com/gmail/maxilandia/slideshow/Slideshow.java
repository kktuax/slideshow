package com.gmail.maxilandia.slideshow;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Slideshow {
	
	private final ExecutorService es = Executors.newSingleThreadExecutor();

	private final List<File> imageFiles;
	
	private final BufferedImage screen;
	
	private final Integer duration;

	private Instant lastDisplayedTime;
	
	private Future<Image> fi;
	
	public void display() {
		Dimension boundary = new Dimension(screen.getWidth(), screen.getHeight());
		Future<Image> fi = es.submit(new ImageLoader(imageFiles.get(0), boundary));
		for(File imageFile : imageFiles.subList(1, imageFiles.size())){
			while(!displayNextImage()){
				tryToSleep();
			}
			try{
				fi.get().display(screen);
				lastDisplayedTime = Instant.now();
			}catch(Exception e){
				LOGGER.warn("Error reading image", e);
			}
			fi = es.submit(new ImageLoader(imageFile, boundary));
		}
	}

	private boolean displayNextImage(){
		if(fi == null || !fi.isDone()){
			return false;
		}else{
			return lastDisplayedTime != null ? 
				Duration.between(lastDisplayedTime, Instant.now()).get(ChronoUnit.SECONDS) >= duration : 
				true;
		}
	}
	
	private static void tryToSleep(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			LOGGER.warn("Interrupted sleep", e);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Slideshow.class);
	
}
