package bg.exploreBG;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
/*@EnableJpaRepositories*/
public class ExploreBgApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExploreBgApplication.class, args);
    }
}
