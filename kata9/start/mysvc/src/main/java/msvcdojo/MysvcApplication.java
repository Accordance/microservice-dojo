package msvcdojo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// tag::code[]
@SpringBootApplication
public class MysvcApplication {

    public static void main(String[] args) {
		SpringApplication.run(MysvcApplication.class, args);
	}
}
// end::code[]

// tag::controller[]
@RestController
class HomeController {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }
}
// end::controller[]
