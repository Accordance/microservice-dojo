package msvcdojo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author Igor Moochnick
 */
@SpringBootApplication
// tag::annotation[]
@EnableConfigServer
public class ConfigurationServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConfigurationServerApplication.class, args);
    }
}
// end::annotation[]
