package com.gmail.maxilandia.slideshow;

import java.awt.Dimension;
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

import com.gmail.maxilandia.slideshow.screen.Screen;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Slideshow {
	
	private final ExecutorService es = Executors.newSingleThreadExecutor();

	private final List<File> imageFiles;
	
	private final Screen screen;
	
	private final Integer duration;

	private Instant lastDisplayedTime;
	
	private Future<Image> fi;
	
	public void display() {
		fi = es.submit(new ImageLoader(imageFiles.get(0), new Dimension(screen.getWidth(), screen.getHeight())));
		for(File imageFile : imageFiles.subList(1, imageFiles.size())){
			while(!displayNextImage()){
				tryToSleep();
			}
			try{
				screen.update(fi.get());
				lastDisplayedTime = Instant.now();
			}catch(Exception e){
				LOGGER.warn("Error reading image", e);
			}
			fi = es.submit(new ImageLoader(imageFile, new Dimension(screen.getWidth(), screen.getHeight())));
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
