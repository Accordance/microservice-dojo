package msvcdojo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

// tag::code[]
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
// end::code[]

// tag::controller[]
@RestController
// tag::api[]
@RequestMapping(value = "/accountsvc")
class AccountController {
  // end::api[]

  static List<Account> account = new ArrayList<Account>();

  static {
    account.add(new Account(1, "John", "john@gmail.com"));
    account.add(new Account(2, "Tim", "tim@gmail.com"));
    account.add(new Account(3, "Mike", "mike@gmail.com"));
  }

  // tag::doc[]
  @RequestMapping(method = GET, path = "/accounts", produces = "application/json")
  ResponseEntity<List<Account>> get() {
    return new ResponseEntity<List<Account>>(account, OK);
  }

  @RequestMapping(method = GET, path = "/accounts/{id}", produces = "application/json")
  ResponseEntity<Account> getById(@PathVariable int id) throws Exception {
    return new ResponseEntity<Account>(account.get(id - 1), OK);
  }

  @RequestMapping(value = "/accounts", method = POST, produces = "application/json", consumes = "application/json")
  ResponseEntity<Account> post(@RequestBody Account body) { //<5>
    account.add(body);
    return new ResponseEntity<Account>(body, CREATED);
  }

  @RequestMapping(value = "/accounts/{id}", method = PUT, produces = "application/json", consumes = "application/json")
  ResponseEntity<Account> put(@PathVariable int id, @RequestBody Account body) {
    account.remove(id - 1);
    account.add(body);
    return new ResponseEntity<Account>(body, OK);
  }

  void delete(@PathVariable int id) throws Exception {
    if (id <= account.size()) {
      account.remove(id - 1);
    } else {
      throw new AccountException("Account not found");
    }
  }
  // end::doc[]
}
// end::controller[]

// tag::model[]
class Account {
  private int id;

  private String name;

  private String email;

  public Account() {
  }

  // end::model[]
  public Account(int id, String name, String email) {
    super();
    this.id = id;
    this.name = name;
    this.email = email;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Account other = (Account) obj;
    if (id != other.id)
      return false;
    return true;
  }

}

class AccountException extends RuntimeException {

    public AccountException(String string) {
        super(string);
    }

}
