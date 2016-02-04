package msvcdojo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

/**
 * @author Igor Moochnick
 */
@SpringBootApplication
public class AccountsServiceApplication {

    public static void main(String[] args) {
            SpringApplication.run(AccountsServiceApplication.class, args);
    }
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
