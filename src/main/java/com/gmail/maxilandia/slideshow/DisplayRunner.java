package com.gmail.maxilandia.slideshow;

import java.io.File;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gmail.maxilandia.slideshow.screen.Screen;

@Component
public class DisplayRunner implements CommandLineRunner {
	
	@Override
	public void run(String... args) throws Exception {
		List<File> imageFiles = Files
			.find(folder.toPath(), Integer.MAX_VALUE, (p, at) -> p.toFile().getName().toLowerCase().endsWith(".jpg"))
			.map(p -> p.toFile())
			.sorted(randomOrder())
			.collect(Collectors.toList());
		LOGGER.info(String.format("Found %s images in %s", imageFiles.size(), folder));
		Slideshow slideshow = new Slideshow(imageFiles, screen, duration); 
		slideshow.display();
	}

	private static Comparator<File> randomOrder() {
		ThreadLocalRandom r = ThreadLocalRandom.current();
		int x = r.nextInt(), y = r.nextInt();
		boolean b = r.nextBoolean();
		return Comparator.comparingInt((File s) -> s.hashCode() ^ x)
			.thenComparingInt(s -> s.getAbsolutePath().length() ^ y)
			.thenComparing(b ? Comparator.naturalOrder() : Comparator.reverseOrder());
	}
	
	@Value("${folder}")
	private File folder;
	
	@Value("${duration}")
	private Integer duration;
	
	@Autowired 
	private Screen screen;

	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayRunner.class);

}
