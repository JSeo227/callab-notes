package dev.collab_sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CollabSyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollabSyncApplication.class, args);
        log.info("Spring Boot Application started");

    }

}
