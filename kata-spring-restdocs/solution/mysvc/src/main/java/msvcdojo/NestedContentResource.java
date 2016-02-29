package msvcdojo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

// tag::NestedContentResource[]
public class NestedContentResource<T> extends ResourceSupport {

  private final Resources<T> nested;

  public NestedContentResource(Iterable<T> toNest) {
    this.nested = new Resources<>(toNest);
  }

  @JsonUnwrapped
  public Resources<T> getNested() {
    return this.nested;
  }
}
// end::NestedContentResource[]
