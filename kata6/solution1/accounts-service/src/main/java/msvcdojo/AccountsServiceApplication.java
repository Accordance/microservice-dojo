package msvcdojo;

/*
// tag::import-discovery-client[]
import com.netflix.discovery.DiscoveryClient;
// end::import-discovery-client[]
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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
public class AccountsServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(AccountsServiceApplication.class, args);
    }
}

// tag::account-resource[]
@Component
class AccountResourceProcessor implements ResourceProcessor<Resource<Account>> {

    ProfilesClient profilesClient;

    @Autowired
    public AccountResourceProcessor(ProfilesClient profilesClient) {
        this.profilesClient = profilesClient;
    }

    @Override
    public Resource<Account> process(Resource<Account> accountResource) {

        Account account = accountResource.getContent();

        URI profileUri = this.profilesClient.getProfileUri(account);

        if (null != profileUri) {
            Link profileLink = new Link(profileUri.toString(), "profile");
            accountResource.add(profileLink);
        }
// end::account-resource[]
// tag::updateAccountWithProfileInfo[]
        ResponseEntity<Profile> profile = this.profilesClient.getProfile(profileUri);
        if (null != profile)
            accountResource.getContent().updateWithProfileData(profile.getBody());
// end::updateAccountWithProfileInfo[]
// tag::account-resource[]
        return accountResource;
    }
}
// end::account-resource[]

/*
// tag::discovery-client[]
@Component
class ProfilesClient {

    private final DiscoveryClient discoveryClient;

    @Autowired
    public ProfilesClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public URI getProfileUri(Account account) {

        InstanceInfo instance = discoveryClient.getNextServerFromEureka(
                "profiles-service", false);

        String url = instance.getHomePageUrl();

        return UriComponentsBuilder.fromHttpUrl( url + "/profiles/{key}")
                .buildAndExpand(account.getUsername()).toUri();
    }
}
// end::discovery-client[]
 */

// tag::ribbon[]
@Component
class ProfilesClient {

    private final LoadBalancerClient loadBalancer;

    @Autowired
    public ProfilesClient(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public URI getProfileUri(Account account) {

        ServiceInstance instance = loadBalancer.choose("profiles-service");
        if (instance == null)
            return null;

        return UriComponentsBuilder.fromHttpUrl( (instance.isSecure() ? "https://" : "http://") +
                instance.getHost() + ":" + instance.getPort() + "/profiles/{key}")
                .buildAndExpand(account.getUsername()).toUri();
    }
// end::ribbon[]
// tag::getProfile[]
    public ResponseEntity<Profile> getProfile(URI profileUri) {

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(profileUri, Profile.class);
    }
// end::getProfile[]
// tag::ribbon[]
}
// end::ribbon[]

// tag::profile[]
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
// end::profile[]

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

// tag::accountProfileInfo[]
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
// end::accountProfileInfo[]

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
