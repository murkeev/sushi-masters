package murkeev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class SushiMastersApplication {

    public static void main(String[] args) {
        SpringApplication.run(SushiMastersApplication.class, args);
    }
}