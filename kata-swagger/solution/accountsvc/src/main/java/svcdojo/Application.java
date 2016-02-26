package svcdojo;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.List;

// tag::code[]
@SpringBootApplication
@EnableSwagger2 //<1>
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean //<2>
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().paths(regex("/accountsvc.*")).build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder().title("Spring REST Sample with Swagger").description("Spring REST Sample with Swagger")
        .termsOfServiceUrl("http://dockerhost:8100/termsofservice").contact("service@ericsson.com")
        .license("Apache License Version 2.0").licenseUrl("http://dockerhost:8100/LICENSE").version("2.0").build();
  }
}

// end::code[]

// tag::controller[]
@RestController
// tag::api[]
@Api(value = "accountsvc", description = "Endpoint for account management") //<1>
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
  @ApiOperation(value = "list accounts", notes = "Lists all accounts. Account informtion contains id, name and email.", produces = "application/json") //<2>
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Account.class) }) //<3>
  @RequestMapping(method = GET, path = "/accounts", produces = "application/json")
  ResponseEntity<List<Account>> get() {
    return new ResponseEntity<List<Account>>(account, OK);
  }

  @ApiOperation(value = "look up  account by id", notes = "Looks up account by id.")
  @ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "Account id", required = true, dataType = "int", paramType = "path", defaultValue = "1") }) //<4>
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Account.class) })
  @RequestMapping(method = GET, path = "/accounts/{id}", produces = "application/json")
  ResponseEntity<Account> getById(@PathVariable int id) throws Exception {
    return new ResponseEntity<Account>(account.get(id - 1), OK);
  }

  @ApiOperation(value = "add new account", nickname = "Add a new account", response = Void.class)
  @ApiResponses(value = { @ApiResponse(code = 201, message = "Success", response = Account.class) })
  @RequestMapping(value = "/accounts", method = POST, produces = "application/json", consumes = "application/json")
  ResponseEntity<Account> post(@ApiParam(value = "Created Account object") @RequestBody Account body) { //<5>
    account.add(body);
    return new ResponseEntity<Account>(body, CREATED);
  }

  @ApiOperation(value = "update an Account", nickname = "Updates existing account with new name and/or email", response = Void.class)
  @ApiResponses(value = { @ApiResponse(code = 201, message = "Success", response = Account.class) })
  @RequestMapping(value = "/accounts/{id}", method = PUT, produces = "application/json", consumes = "application/json")
  ResponseEntity<Account> put(@PathVariable int id,
      @ApiParam(value = "Updated Account object") @RequestBody Account body) {
    account.remove(id - 1);
    account.add(body);
    return new ResponseEntity<Account>(body, OK);
  }

  @ApiOperation(value = "delete account", notes = "Deletes account by id")
  @RequestMapping(value = "/accounts/{id}", method = DELETE)
  @ApiResponses(value = { @ApiResponse(code = 400, message = "Account not found", response = void.class) })
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
@ApiModel( value = "Account", description = "Account resource representation" ) //<6>
class Account {
  @ApiModelProperty( value = "Account id", required = true )
  private int id;

  @ApiModelProperty( value = "Account name", required = true )
  private String name;

  @ApiModelProperty( value = "Account email", required = false )
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