package msvcdojo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// tag::AccountController[]
@RestController
@RequestMapping(value = "/accounts", produces = "application/json")
public class AccountController {

  @Autowired
  private AccountResourceAssembler accountResourceAssembler;
  @Autowired
  private ContactResourceAssembler contactResourceAssembler;

  List<Account> accounts = Lists.newArrayList(
      new Account(1, "John"),
      new Account(2, "Tim"),
      new Account(3, "Mike"));

  List<Contact> contacts = Lists.newArrayList(
      new Contact(1, "Waltham, MA"),
      new Contact(2, "Boston, MA")
  );

  @RequestMapping(method = GET)
  NestedContentResource<AccountResourceAssembler.AccountResource> getAccounts() {
    //Link accountsLink = linkTo(AccountController.class).slash("/accounts").withSelfRel();
    NestedContentResource<AccountResourceAssembler.AccountResource> nestedContentResource =
        new NestedContentResource<>
            (this.accountResourceAssembler.toResources(accounts));
    nestedContentResource.add(linkTo(AccountController.class).withSelfRel());
    return nestedContentResource;
  }

  @RequestMapping(value = "/{id}", method = GET)
  AccountResourceAssembler.AccountResource getAccount(@PathVariable("id") int id) {
    return this.accountResourceAssembler.toResource(accounts.get(id-1));
  }

  @RequestMapping(value = "/{id}/contacts", method = GET)
  NestedContentResource<ContactResourceAssembler.ContactResource> getAccountContacts(@PathVariable("id") int id) {
    return new NestedContentResource<>
        (this.contactResourceAssembler.toResources(contacts));
  }

}
// end::AccountController[]
