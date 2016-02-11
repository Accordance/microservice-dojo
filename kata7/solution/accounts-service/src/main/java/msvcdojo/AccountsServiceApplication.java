package msvcdojo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import feign.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.net.URI;
import java.util.List;

/**
 * @author Igor Moochnick
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
// tag::enable-hystrix[]
@EnableHystrix
//@EnableCircuitBreaker
public class AccountsServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(AccountsServiceApplication.class, args);
    }
}
// end::enable-hystrix[]

@Component
class AccountResourceProcessor implements ResourceProcessor<Resource<Account>> {

    ProfilesClient profilesClient;

    ProfilesServiceHystrixProxy profilesServiceProxy;

    @Autowired
    public AccountResourceProcessor(ProfilesClient profilesClient, ProfilesServiceHystrixProxy profilesServiceProxy) {
        this.profilesClient = profilesClient;
        this.profilesServiceProxy = profilesServiceProxy;
    }

    @Override
    public Resource<Account> process(Resource<Account> accountResource) {

        Account account = accountResource.getContent();

        URI profileUri = this.profilesClient.getProfileUri(account);

        if (null != profileUri) {
            Link profileLink = new Link(profileUri.toString(), "profile");
            accountResource.add(profileLink);
        }

        ResponseEntity<Profile> profile = profilesServiceProxy.getProfile(account.getUsername());
        if (null != profile)
            accountResource.getContent().updateWithProfileData(profile.getBody());

        return accountResource;
    }
}

@FeignClient("profiles-service")
interface ProfilesServiceProxy {

    @RequestMapping(method = RequestMethod.GET, value = "/profiles/{key}")
    ResponseEntity<Profile> getProfile(@PathVariable("key") String key);
}

@Service
class ProfilesServiceHystrixProxy {

    private final ProfilesServiceProxy profilesServiceProxy;

    @Autowired
    public ProfilesServiceHystrixProxy(ProfilesServiceProxy profilesServiceProxy) {
        this.profilesServiceProxy = profilesServiceProxy;
    }

    @HystrixCommand(groupKey = "profiles-service", commandKey = "getProfile", fallbackMethod = "getProfileFallback")
    public ResponseEntity<Profile> getProfile(String key) {
        return profilesServiceProxy.getProfile(key);
    }

    public ResponseEntity<Profile> getProfileFallback(String key) {
        return null;
    }
}

@Component
class ProfilesClient {

    private final LoadBalancerClient loadBalancer;

    @Autowired
    public ProfilesClient(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public Link defaultGetProfileUri(Account account) {
        return null;
    }

    @HystrixCommand(fallbackMethod = "defaultGetProfileUri")
    public URI getProfileUri(Account account) {

        ServiceInstance instance = loadBalancer.choose("profiles-service");
        if (instance == null)
            return null;

        return UriComponentsBuilder.fromHttpUrl( (instance.isSecure() ? "https://" : "http://") +
                instance.getHost() + ":" + instance.getPort() + "/profiles/{key}")
                .buildAndExpand(account.getUsername()).toUri();
    }
}

class Profile {

    private String key;
    private String fullName;
    private Integer photoCount;

    public void setKey(String key) { this.key = key; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void addPhotoCount(Integer photoCount) { this.photoCount = photoCount; }

    public String getKey() { return key; }
    public String getFullName() {
        return fullName;
    }
    public Integer getPhotoCount() { return photoCount; }
}

@RepositoryRestResource
interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUsername(@Param("username") String username);

    List<Account> findByRole(@Param("role") String role);
}

@Entity
class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String role;
    public String getRole() { return role; }

    Account() { // JPA only
    }

    public String getUsername() {
        return username;
    }
    public Long getId() {
        return id;
    }
	
    @Transient
    private String fullName;
    @Transient
    private Integer photoCount;

    public String getFullName() {
        return fullName;
    }
    public Integer getPhotoCount() { return photoCount; }

    public void updateWithProfileData(Profile profile) {
        this.fullName = profile.getFullName();
        this.photoCount = profile.getPhotoCount();
    }

    public Account(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", role='").append(role).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
