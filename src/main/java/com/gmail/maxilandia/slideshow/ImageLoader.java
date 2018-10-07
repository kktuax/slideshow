package com.gmail.maxilandia.slideshow;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ImageLoader implements Callable<Image> {

	private final File imgFile;
	
	private final Dimension boundary;
	
	@Override
	public Image call() throws Exception {
		BufferedImage oImg = readAndRotate();
		Dimension imgSize = new Dimension(oImg.getWidth(), oImg.getHeight());
		int original_width = imgSize.width, original_height = imgSize.height;
		int new_width = original_width, new_height = original_height;
		if (original_width > boundary.width) {
			new_width = boundary.width; // scale width to fit
			new_height = (new_width * original_height) / original_width; // scale height to maintain aspect ratio
		}
		if (new_height > boundary.height) {
			new_height = boundary.height; // scale height to fit instead
			new_width = (new_height * original_width) / original_height; // scale width to maintain aspect ratio
		}
		return new Image(imgFile, Scalr.resize(oImg, new_width, new_height));
	}
	
	private BufferedImage readAndRotate() throws IOException {
		BufferedImage image = ImageIO.read(imgFile);
		try {
			Optional<Rotation> rotation = ImageInformation.readImageInformation(imgFile)
				.getRotation();
			if(!rotation.isPresent()){
				return image;
			}else{
				return Scalr.rotate(image, rotation.get());
			}
		} catch (MetadataException | ImageProcessingException e) {
			LOGGER.warn(String.format("Problem reading rotation information: %s", e.getMessage()));
			return image;
		}		
	}

	@RequiredArgsConstructor
	private static class ImageInformation {
		
		private final int orientation;
		
		private Optional<Rotation> getRotation() {
			switch (orientation) {
			case 3: // PI rotation
				LOGGER.info(String.format("Defined rotation %s for orientation: %s", Rotation.CW_180, orientation));
				return Optional.of(Rotation.CW_180);
			case 6: // -PI/2 and -width
				LOGGER.info(String.format("Defined rotation %s for orientation: %s", Rotation.CW_270, orientation));
				return Optional.of(Rotation.CW_90);
			case 8: // PI / 2
				LOGGER.info(String.format("Defined rotation %s for orientation: %s", Rotation.CW_90, orientation));
				return Optional.of(Rotation.CW_270);
			}
			return Optional.empty();
		}
		
		public static ImageInformation readImageInformation(File imageFile) throws IOException, MetadataException, ImageProcessingException {
			int orientation = ImageMetadataReader.readMetadata(imageFile)
				.getFirstDirectoryOfType(ExifIFD0Directory.class)
				.getInt(ExifIFD0Directory.TAG_ORIENTATION);
			return new ImageInformation(orientation);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageLoader.class);

}
