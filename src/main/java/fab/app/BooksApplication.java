package fab.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"fab"})  // package where HTTP handlers, repositories, etc. are defined
@EntityScan(basePackages = {"fab.model"})  // package where Book model is defined
@EnableJpaRepositories(basePackages = {"fab.database"}) // package where BookRepository is defined
public class BooksApplication {

  public static void main(String[] args) {
    SpringApplication.run(BooksApplication.class, args);
  }
}
