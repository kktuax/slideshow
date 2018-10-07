package com.gmail.maxilandia.slideshow;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tw.pi.NarSystem;
import org.tw.pi.framebuffer.FrameBuffer;

@Component
public class DisplayRunner implements CommandLineRunner {

	private final Random rand = new Random();
	
	private final ExecutorService es = Executors.newSingleThreadExecutor();
	
	@Override
	public void run(String... args) throws Exception {
		NarSystem.loadLibrary();
		FrameBuffer frameBuffer = new FrameBuffer(fbLocation);
		List<File> imageFiles = Files
			.find(folder.toPath(), Integer.MAX_VALUE, (p, at) -> p.toFile().getName().toLowerCase().endsWith(".jpg"))
			.map(p -> p.toFile())
			.collect(Collectors.toList());
		LOGGER.info(String.format("Found %s images in %s", imageFiles.size(), folder));
		try{
			BufferedImage screen = frameBuffer.getScreen();
			Dimension boundary = new Dimension(screen.getWidth(), screen.getHeight());
			LOGGER.info(String.format("Found resolution of %sx%s", screen.getWidth(), screen.getHeight()));
			Future<Image> fi = es.submit(new ImageLoader(imageFiles.get(rand.nextInt(imageFiles.size())), boundary));
			while (true) {
				boolean displayNextImage = false;
				if(fi.isDone()){
					displayNextImage = lastDisplayedTime != null ? 
						Duration.between(lastDisplayedTime, Instant.now()).get(ChronoUnit.SECONDS) >= duration : 
						true;
				}
				if(!displayNextImage){
					Thread.sleep(100);
				}else{
					try{
						fi.get().display(screen);
						lastDisplayedTime = Instant.now();
					}catch(Exception e){
						LOGGER.warn("Error reading image", e);
					}
					fi = es.submit(new ImageLoader(imageFiles.get(rand.nextInt(imageFiles.size())), boundary));
				}				
			}
		}finally{
			frameBuffer.close();
		}
	}
	
	private Instant lastDisplayedTime;
	
	@Value("${folder}")
	private File folder;
	
	@Value("${duration}")
	private Integer duration;
	
	@Value("${frame-buffer-location}")
	private String fbLocation;

	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayRunner.class);

}
