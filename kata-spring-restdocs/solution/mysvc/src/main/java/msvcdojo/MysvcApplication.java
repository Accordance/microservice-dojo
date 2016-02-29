package msvcdojo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// tag::spring-boot-app[]
@SpringBootApplication
public class MysvcApplication {

  public static void main(String[] args) {
    SpringApplication.run(MysvcApplication.class, args);
  }
}
// end::spring-boot-app[]