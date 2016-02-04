package msvcdojo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableEurekaClient
// tag::enable-zuul[]
@EnableZuulProxy
public class GatewayServiceApplication {

    public static void main(String args[]) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
// end::enable-zuul[]
