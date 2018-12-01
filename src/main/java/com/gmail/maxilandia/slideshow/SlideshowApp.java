package com.gmail.maxilandia.slideshow;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.gmail.maxilandia.slideshow.screen.FbScreen;
import com.gmail.maxilandia.slideshow.screen.JFrameScreen;
import com.gmail.maxilandia.slideshow.screen.Screen;

@SpringBootApplication
public class SlideshowApp {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SlideshowApp.class, args);
	}
	
	@Bean
	public static Screen screen(@Value("${frame-buffer-location}") String fbLocation){
		if(fbLocation != null && !fbLocation.trim().isEmpty()){
			return new FbScreen(fbLocation);
		}else{
			return new JFrameScreen(800, 600);
		}
	}
	
	@Bean
	public static CommandLineRunner runner(
			@Value("${folder}") File folder,
			@Value("${duration}") Integer duration,
			@Autowired Screen screen
	){
		return args -> {
			List<File> imageFiles = ImageFinder.find(folder);
			Slideshow slideshow = new Slideshow(imageFiles, screen, duration); 
			slideshow.display();
		};
	}
	
}
