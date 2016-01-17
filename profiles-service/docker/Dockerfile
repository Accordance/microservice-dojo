FROM java:8
MAINTAINER Igor Moochnick "igor@igorshare.com"
VOLUME /tmp
ADD profiles-service.jar accounts-service.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom" ,"-jar", "/profiles-service.jar" ]
