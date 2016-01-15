FROM java:8
MAINTAINER Igor Moochnick "igor@igorshare.com"
VOLUME /tmp
ADD accounts-service.jar accounts-service.jar
# RUN bash -c 'touch /accounts-service.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom" ,"-jar", "/accounts-service.jar" ]
