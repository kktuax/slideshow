package com.gmail.maxilandia.slideshow;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;

import lombok.Data;
import lombok.RequiredArgsConstructor;

class ImageLoader {

	public static BufferedImage load(File imgFile) throws IOException, MetadataException, ImageProcessingException{
		BufferedImage image = ImageIO.read(imgFile);
		Optional<AffineTransform> transform = ImageInformation.readImageInformation(imgFile)
			.getExifTransformation();
		if(!transform.isPresent()){
			return image;
		}else{
			AffineTransformOp op = new AffineTransformOp(transform.get(), AffineTransformOp.TYPE_BICUBIC);
			BufferedImage destinationImage = op.createCompatibleDestImage(image, (image.getType() == BufferedImage.TYPE_BYTE_GRAY) ? image.getColorModel() : null);
			Graphics2D g = destinationImage.createGraphics();
			g.setBackground(Color.WHITE);
			g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
			destinationImage = op.filter(image, destinationImage);
			return destinationImage;
		}
	}

	@Data
	@RequiredArgsConstructor
	private static class ImageInformation {
		
		private final int orientation, width, height;
		
		public Optional<AffineTransform> getExifTransformation() {
			if(orientation == 1){
				return Optional.empty();
			}
			AffineTransform t = new AffineTransform();
			switch (orientation) {
			case 2: // Flip X
				t.scale(-1.0, 1.0);
				t.translate(-width, 0);
				break;
			case 3: // PI rotation
				t.translate(width, height);
				t.rotate(Math.PI);
				break;
			case 4: // Flip Y
				t.scale(1.0, -1.0);
				t.translate(0, -height);
				break;
			case 5: // - PI/2 and Flip X
				t.rotate(-Math.PI / 2);
				t.scale(-1.0, 1.0);
				break;
			case 6: // -PI/2 and -width
				t.translate(height, 0);
				t.rotate(Math.PI / 2);
				break;
			case 7: // PI/2 and Flip
				t.scale(-1.0, 1.0);
				t.translate(-height, 0);
				t.translate(0, width);
				t.rotate(3 * Math.PI / 2);
				break;
			case 8: // PI / 2
				t.translate(0, width);
				t.rotate(3 * Math.PI / 2);
				break;
			}
			LOGGER.info(String.format("Defined AffineTransform for orientation: %s", orientation));
			return Optional.of(t);
		}
		
		public static ImageInformation readImageInformation(File imageFile) throws IOException, MetadataException, ImageProcessingException {
			Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
			Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
			int orientation = 1;
			try {
				orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
			} catch (MetadataException me) {
				LOGGER.warn("Could not get orientation");
			}
			int width = jpegDirectory.getImageWidth();
			int height = jpegDirectory.getImageHeight();
			return new ImageInformation(orientation, width, height);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageLoader.class);

}
