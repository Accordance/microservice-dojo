package msvcdojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.AbstractResource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author Igor Moochnick
 * Inspired by https://github.com/joshlong/spring-doge-microservice/tree/master/doge-service
 */
// tag::eureka[]
@SpringBootApplication
@EnableEurekaClient
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class ProfilesServiceApplication {
// end::eureka[]
    @Bean
    CommandLineRunner init(ProfilesRepository profilesRepository) {
        return a -> profilesRepository.deleteAll();
    }

    @Bean
    public ResourceProcessor<Resource<Profile>> personProcessor() {

        return new ResourceProcessor<Resource<Profile>>() {

            @Override
            public Resource<Profile> process(Resource<Profile> resource) {

                resource.add(linkTo(methodOn(ProfilePhotoController.class).readPhotos(resource.getContent().getKey())).withRel("photos"));

                return resource;
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ProfilesServiceApplication.class, args);
    }
}

@RepositoryRestResource
interface ProfilesRepository extends MongoRepository<Profile, String> {

    @Query("{ '_id' : ?0 }")
    Profile findByKey(@Param("key") String key);

    Profile findByFullName(@Param("address") String address);

}

@RestController
@RequestMapping(value = "/profiles/{key}/photos", produces = MediaType.APPLICATION_JSON_VALUE)
class ProfilePhotoController {

    private final ProfilesRepository profilesRepository;
    private final GridFsTemplate fs;

    @RequestMapping(method = RequestMethod.GET)
    Collection<Resource<String>> readPhotos(@PathVariable String key) {

        Profile profile = this.profilesRepository.findOne(key);

        Collection<Resource<String>> coll = new LinkedList<>();

        profile.photosList().forEach(photoId -> {
            Resource<String> resource = new Resource<>(photoId);
            resource.add(new Link(ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}/photo").buildAndExpand(photoId).toString(), photoId));

            coll.add(resource);
        });

        return coll;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Resource<Profile> readPhoto(@PathVariable String id) {
        Profile profilePhoto = this.profilesRepository.findOne(id);
        return new Resource<Profile>(profilePhoto);
    }

    @RequestMapping(value = "/{id}/photo", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<PhotoResource> readPhotoResource(@PathVariable String id) {
        Photo photo = () -> this.fs.getResource(id).getInputStream();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(new PhotoResource(photo), httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    ResponseEntity<Resource<Profile>> insertPhoto(@PathVariable String key,
                                                       @RequestParam MultipartFile file) throws IOException {
        Photo photo = file::getInputStream;
        Profile profile = this.profilesRepository.findOne(key);
        String id = key + profile.getPhotoCount();
        try (InputStream inputStream = photo.getInputStream()) {
            this.fs.store(inputStream, id);
        }
        profile.addPhotoReference(id);
        this.profilesRepository.save(profile);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}/photo").buildAndExpand(id).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return new ResponseEntity<>(
                this.readPhoto(key), headers, HttpStatus.CREATED);
    }

    @Autowired
    public ProfilePhotoController(ProfilesRepository repository, GridFsTemplate gridFileSystem) {
        this.profilesRepository = repository;
        this.fs = gridFileSystem;
    }

}

@Document(collection="profiles")
class Profile {
    public Profile() {
    }

    @Id
    private String id;
    @Indexed
    private String fullName;
    private List<String> photos;

    public void setId(String id) {
        this.id = id;
    }
    public void setKey(String key) {
        this.id = key;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void addPhotoReference(String photoId) {
        this.photosList().add(photoId);
    }

    public String getKey() {
        return id;
    }
    public String getFullName() {
        return fullName;
    }
    public Integer getPhotoCount() { return this.photosList().size(); }
    public List<String> photosList() {
        if (this.photos == null)
            this.photos = new ArrayList<>();
        return this.photos;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%s, fullName='%s']",
                id, fullName);
    }
}


class PhotoResource extends AbstractResource {

    private final Photo photo;

    public PhotoResource(Photo photo) {
        Assert.notNull(photo, "Photo must not be null");
        this.photo = photo;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.photo.getInputStream();
    }

    @Override
    public long contentLength() throws IOException {
        return -1;
    }

}

interface Photo {

    /**
     * @return a new {@link InputStream} containing photo data as a JPEG. The caller is
     * responsible for closing the stream.
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException;

}
