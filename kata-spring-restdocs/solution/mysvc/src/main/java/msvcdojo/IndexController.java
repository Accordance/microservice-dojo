package msvcdojo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {

  @RequestMapping(method= RequestMethod.GET)
  public ResourceSupport index() {
    ResourceSupport index = new ResourceSupport();
    index.add(linkTo(AccountController.class).withRel("accounts"));
    index.add(linkTo(ContactController.class).withRel("contacts"));
    return index;
  }

}
