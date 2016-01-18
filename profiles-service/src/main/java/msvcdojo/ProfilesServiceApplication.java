package msvcdojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.AbstractResource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
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
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Igor Moochnick
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableEurekaClient
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class ProfilesServiceApplication {

    @Bean
    CommandLineRunner init(ProfilePhotoRepository profilePhotoRepository) {
        return a -> profilePhotoRepository.deleteAll();
    }

    public static void main(String[] args) {
        SpringApplication.run(ProfilesServiceApplication.class, args);
    }
}

interface ProfilePhotoRepository extends MongoRepository<ProfilePhoto, String> {

    Collection<ProfilePhoto> findByKey(String key);

    ProfilePhoto findByKeyAndId(String key, String id);
}

@RestController
@RequestMapping(value = "/profiles/{key}/photos", produces = MediaType.APPLICATION_JSON_VALUE)
class ProfilePhotoController {

    private final ProfilePhotoRepository profilePhotoRepository;
    private final GridFsTemplate fs;

    @RequestMapping(method = RequestMethod.GET)
    Collection<Resource<ProfilePhoto>> readPhotos(@PathVariable String key) {

        Collection<Resource<ProfilePhoto>> coll = new LinkedList<>();

        for (ProfilePhoto photo : this.profilePhotoRepository.findByKey(key)) {
            Resource<ProfilePhoto> resource = new Resource<>(photo);
            resource.add(new Link(ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}/photo").buildAndExpand(photo.getId()).toString(), "photo"));
            coll.add(resource);
        }
        return coll;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Resource<ProfilePhoto> readPhoto(@PathVariable String key, @PathVariable String id) {
        ProfilePhoto profilePhoto = this.profilePhotoRepository.findByKeyAndId(key, id);
        return new Resource<ProfilePhoto>(profilePhoto);
    }

    @RequestMapping(value = "/{id}/photo", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<PhotoResource> readPhotoResource(@PathVariable String key, @PathVariable String id) {
        ProfilePhoto profilePhoto = this.profilePhotoRepository.findByKeyAndId(key, id);
        Photo photo = () -> this.fs.getResource(profilePhoto.getId()).getInputStream();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(new PhotoResource(photo), httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    ResponseEntity<Resource<ProfilePhoto>> insertPhoto(@PathVariable String key,
                                                       @RequestParam MultipartFile file) throws IOException {
        Photo photo = file::getInputStream;
        ProfilePhoto profilePhoto = this.profilePhotoRepository.save(new ProfilePhoto(key));
        String id = profilePhoto.getId();
        try (InputStream inputStream = photo.getInputStream()) {
            this.fs.store(inputStream, id);
        }
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}/photo").buildAndExpand(id).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return new ResponseEntity<>(
                this.readPhoto(key, id), headers, HttpStatus.CREATED);
    }

    @Autowired
    public ProfilePhotoController(ProfilePhotoRepository repository, GridFsTemplate gridFileSystem) {
        this.profilePhotoRepository = repository;
        this.fs = gridFileSystem;
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

class ProfilePhoto {

    public ProfilePhoto() {
    }

    public ProfilePhoto(String key) {
        this.key = key;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Id
    private String id;
    private String key;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
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
