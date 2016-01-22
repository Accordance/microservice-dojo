package msvcdojo;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang.StringUtils;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

/**
 * @author Igor Moochnick
 */
@SpringBootApplication
@EnableEurekaClient
@EnableAutoConfiguration
@EnableHystrix
public class AccountsServiceApplication {

    public static void main(String[] args) {

        if (args.length > 0) {

            String sql_server  = System.getProperty("sql_server");
            if (sql_server == null) sql_server = "localhost";
            String db_name  = System.getProperty("db_name");
            if (db_name == null) db_name = "demo";
            String user  = System.getProperty("user");
            if (user == null) user = "sa";
            String password  = System.getProperty("pass");

            String data_source = String.format("jdbc:mysql://%s:3306/%s", sql_server, db_name);

            System.out.printf("Migrating data into SQL: %s\n", data_source);

            Flyway flyway = new Flyway();
            flyway.setDataSource(data_source, user, password);

            if ("migrate".equals(args[0])) {
                flyway.migrate();
            } else if ("clean".equals(args[0])) {
                flyway.clean();
            }

        } else {

            SpringApplication.run(AccountsServiceApplication.class, args);
        }
    }
}

@Component
class AccountResourceProcessor implements ResourceProcessor<Resource<Account>> {

    private final ProfilesClient profilesClient;

    @Autowired
    public AccountResourceProcessor(ProfilesClient profilesClient) {
        this.profilesClient = profilesClient;
    }

    @Override
    public Resource<Account> process(Resource<Account> accountResource) {

        HttpServletRequest currentRequest = getCurrentRequest();

        org.springframework.hateoas.Link profileLink = this.profilesClient.buildProfileLink(
                accountResource.getContent(), getCurrentRequest());
        if (null != profileLink)
            accountResource.add(profileLink);

        return accountResource;
    }

    private static HttpServletRequest getCurrentRequest() {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        return servletRequest;
    }
}

@Component
class ProfilesClient {

    private final DiscoveryClient discoveryClient;

    @Autowired
    public ProfilesClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public Link defaultProfileLink(Account account, HttpServletRequest currentRequest) {
        return null;
    }

    @HystrixCommand(fallbackMethod = "defaultProfileLink")
    public Link buildProfileLink(Account account, HttpServletRequest currentRequest) {

        String url = uriBuilderWithForward("profiles-service", currentRequest)
                .path("/profiles/{key}/photos")
                .buildAndExpand(Long.toString(account.getId())).toUriString();

        return new Link(url, "profile");
    }

    private UriComponentsBuilder uriBuilderWithForward(String serviceId, HttpServletRequest currentRequest) {

        String[] serviceNameParts = serviceId.split("-"); // Our convention

        InstanceInfo instance = discoveryClient.getNextServerFromEureka(
                serviceId, false);

        URI uri = URI.create(instance.getHomePageUrl());
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(uri);

        String forwardHost = currentRequest.getHeader("X-Forwarded-Host");
        if (StringUtils.isNotEmpty(forwardHost)) {
            String[] parts = forwardHost.split(":");
            uriBuilder.host(parts[0]);
            if (parts.length > 1)
                uriBuilder.port(parts[1]);
        }

        String forwardPrefix = currentRequest.getHeader("X-Forwarded-Prefix");
        if (StringUtils.isNotEmpty(forwardPrefix)) {
            uriBuilder.replacePath(serviceNameParts[0]); // Our convention
            uriBuilder.path(uri.getPath());
        }

        return uriBuilder;
    }
}

@RepositoryRestResource
interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUsername(@Param("username") String username);
}

@Entity
class Account {

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    Account() { // JPA only
    }

    public Account(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
