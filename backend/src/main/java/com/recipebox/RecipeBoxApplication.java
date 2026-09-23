package com.recipebox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Recipe Box - the app the Basic Playwright course's graded task is written
 * against. Students RUN this backend ( in docker ); they never need to read it.
 */
@SpringBootApplication
@EnableScheduling
public class RecipeBoxApplication {

    public static void main( String[] args ) {
        SpringApplication.run( RecipeBoxApplication.class, args );
    }
}
