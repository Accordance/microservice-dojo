package msvcdojo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Configuration;

/**
 * @author Igor Moochnick
 */
@Configuration
@EnableAutoConfiguration
@EnableConfigServer
public class ConfigurationServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConfigurationServerApplication.class, args);
    }
}
