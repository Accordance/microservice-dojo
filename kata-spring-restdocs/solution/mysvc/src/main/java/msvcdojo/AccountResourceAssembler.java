package msvcdojo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

// tag::ResourceAssembler[]
@Component
public class AccountResourceAssembler
    extends ResourceAssemblerSupport<Account, AccountResourceAssembler.AccountResource> {

  public AccountResourceAssembler() {
    super(AccountController.class, AccountResource.class);
  }


  @Override
  public AccountResource toResource(Account entity) {
    AccountResource resource = createResourceWithId(entity.getId(), entity);
    resource.add(linkTo(AccountController.class).slash(entity.getId()).slash("contacts").withRel("account-contacts"));
    return resource;
  }

  @Override
  protected AccountResource instantiateResource(Account entity) {
    return new AccountResource(entity);
  }

  static class AccountResource extends Resource<Account> {
    public AccountResource(Account account) {
      super(account);
    }
  }

}
// end::ResourceAssembler[]