package msvcdojo;

import org.flywaydb.core.Flyway;
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

            switch (args[0]) {
                case "migrate": flyway.migrate(); break;
                case "clean": flyway.clean(); break;
            }

        } else {

            SpringApplication.run(AccountsServiceApplication.class, args);
        }
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
