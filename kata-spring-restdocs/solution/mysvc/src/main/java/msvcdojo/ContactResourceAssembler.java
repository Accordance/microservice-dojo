package msvcdojo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

// tag::ResourceAssembler[]
@Component
public class ContactResourceAssembler
    extends ResourceAssemblerSupport<Contact, ContactResourceAssembler.ContactResource> {

  public ContactResourceAssembler() {
    super(ContactController.class, ContactResource.class);
  }


  @Override
  public ContactResource toResource(Contact entity) {
    ContactResource resource = createResourceWithId(entity.getId(), entity);
    resource.add(linkTo(ContactController.class).slash(entity.getId()).slash("accounts").withRel("contact-accounts"));
    return resource;
  }

  @Override
  protected ContactResource instantiateResource(Contact entity) {
    return new ContactResource(entity);
  }

  static class ContactResource extends Resource<Contact> {
    public ContactResource(Contact contact) {
      super(contact);
    }
  }

}
// end::ResourceAssembler[]