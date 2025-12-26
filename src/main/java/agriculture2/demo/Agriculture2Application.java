package agriculture2.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;


@SuppressWarnings("unused")
@SpringBootApplication
@EnableScheduling

public class Agriculture2Application {

	public static void main(String[] args) {
		SpringApplication.run(Agriculture2Application.class, args);
	}

}
