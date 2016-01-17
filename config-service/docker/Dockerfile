FROM java:8
MAINTAINER Igor Moochnick "igor@igorshare.com"
VOLUME /tmp
ADD config-service.jar config-service.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom" ,"-jar", "/config-service.jar" ]
