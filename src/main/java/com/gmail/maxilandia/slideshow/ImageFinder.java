package com.gmail.maxilandia.slideshow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ImageFinder {
	
	private ImageFinder(){}
	
	public static List<File> find(File folder) throws IOException {
		List<File> imageFiles = Files
			.find(folder.toPath(), Integer.MAX_VALUE, (p, at) -> p.toFile().getName().toLowerCase().endsWith(".jpg"))
			.map(p -> p.toFile())
			.sorted(randomOrder())
			.collect(Collectors.toList());
		LOGGER.info(String.format("Found %s images in %s", imageFiles.size(), folder));
		return imageFiles;
	}

	private static Comparator<File> randomOrder() {
		ThreadLocalRandom r = ThreadLocalRandom.current();
		int x = r.nextInt(), y = r.nextInt();
		boolean b = r.nextBoolean();
		return Comparator.comparingInt((File s) -> s.hashCode() ^ x)
			.thenComparingInt(s -> s.getAbsolutePath().length() ^ y)
			.thenComparing(b ? Comparator.naturalOrder() : Comparator.reverseOrder());
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageFinder.class);

}
