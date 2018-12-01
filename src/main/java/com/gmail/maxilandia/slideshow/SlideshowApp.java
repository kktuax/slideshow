package com.gmail.maxilandia.slideshow;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.gmail.maxilandia.slideshow.screen.FbScreen;
import com.gmail.maxilandia.slideshow.screen.Screen;

@SpringBootApplication
public class SlideshowApp {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SlideshowApp.class, args);
	}
	
	@Bean
	public static Screen screen(@Value("${frame-buffer-location}") String fbLocation){
		return new FbScreen(fbLocation);
	}
	
}
