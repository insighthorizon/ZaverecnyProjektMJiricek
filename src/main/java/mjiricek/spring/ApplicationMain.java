package mjiricek.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The class with main method annotated as spring boot application
 */
@SpringBootApplication
public class ApplicationMain {
    /**
     * main method
     * @param args arguments passed by the caller
     */
    public static void main(String[] args) {
        // start the spring boot application
        SpringApplication.run(ApplicationMain.class, args);
    }
}
