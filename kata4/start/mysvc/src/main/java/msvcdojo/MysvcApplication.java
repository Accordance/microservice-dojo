package msvcdojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MysvcApplication {

    public static void main(String[] args) {
		SpringApplication.run(MysvcApplication.class, args);
	}
}

// tag::controller[]
@RestController
class HomeController {

    private String name = "World";

    @RequestMapping("/")
    String home() {
        return "Hello, " + name + "!";
    }
}
// end::controller[]