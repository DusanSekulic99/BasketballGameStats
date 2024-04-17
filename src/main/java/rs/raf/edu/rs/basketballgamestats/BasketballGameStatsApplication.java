package rs.raf.edu.rs.basketballgamestats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class BasketballGameStatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasketballGameStatsApplication.class, args);
	}

}
