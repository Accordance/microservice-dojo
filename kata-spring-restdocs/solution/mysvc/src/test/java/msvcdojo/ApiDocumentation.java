package msvcdojo;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MysvcApplication.class)
@WebAppConfiguration
public class ApiDocumentation {

  //tag::rest-docs-config[]
  @Rule
  public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets"); //<1>
  private RestDocumentationResultHandler document; //<2>
  @Autowired
  private WebApplicationContext context;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    this.document = document("{method-name}/",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint())); //<3>

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
        .apply(documentationConfiguration(this.restDocumentation)) //<4>
        .alwaysDo(this.document)
        .build();
  }

  @Test
  public void getAccountsSimpleDoc() throws Exception {
    this.mockMvc.perform(get("/accounts"))
        .andExpect(status().isOk())
        .andDo(this.document); //<5>
  }
  //end::rest-docs-config[]

  //tag::rest-docs-impl[]
  @Test
  public void getIndexExample() throws Exception {
    this.document.snippets(
        links( //<1>
            linkWithRel("accounts").description("The <<resources-accounts,Account Resource>>"),
            linkWithRel("contacts").description("The <<resources-contacts,Contact Resource>>")),
        responseFields( //<2>
            fieldWithPath("_links").description("<<resources-index-links,Links>> to other resources")));

    this.mockMvc.perform(get("/"))
        .andExpect(status().isOk());
  }

  @Test
  public void getAccountsExample() throws Exception {
    this.document.snippets(
        responseFields(
            fieldWithPath("_links.self").description("Resource Self Link"),
            fieldWithPath("_embedded.accountList").description("An array of <<resources-account,Account resources>>")));

    this.mockMvc.perform(get("/accounts"))
        .andExpect(status().isOk());
  }

  @Test
  public void getAccountExample() throws Exception {
    this.document.snippets(
        responseFields(
            fieldWithPath("id").description("Account unique identifier"),
            fieldWithPath("name").description("Account name"),
            fieldWithPath("_links.self").description("Account Resource Self Link"),
            fieldWithPath("_links.account-contacts").description("Contacts associated for given Account")));

    this.mockMvc.perform(get("/accounts/1"))
        .andExpect(status().isOk());
//        .andExpect(jsonPath("id", is(notNullValue())))
//        .andExpect(jsonPath("name", is(notNullValue())))
//        .andExpect(jsonPath("_links.self", is(notNullValue())))
//        .andExpect(jsonPath("_links.account-contacts", is(notNullValue())));
  }
  //end::rest-docs-impl[]

  @Test
  public void getContactExample() throws Exception {

    this.document.snippets(
        responseFields(
            //fieldWithPath("_links.self").description("Contact Resource Self Link"),
            fieldWithPath("_embedded.contactList").description("An array of <<resources-contact,Contact resources>>")));

    this.mockMvc.perform(get("/accounts/1/contacts"))
        .andExpect(status().isOk())
        //.andExpect(jsonPath("_links.self", is(notNullValue())))
        .andExpect(jsonPath("_embedded.contactList", is(notNullValue())));
  }

}