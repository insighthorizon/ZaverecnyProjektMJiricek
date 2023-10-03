package mjiricek.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The class with main method
 * annotated as spring boot application
 */
@SpringBootApplication
public class ApplicationMain {
    public static void main(String[] args) {
        // application entry point
        SpringApplication.run(ApplicationMain.class, args);
    }
}
