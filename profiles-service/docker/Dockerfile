FROM java:8
MAINTAINER Igor Moochnick "igor@igorshare.com"
VOLUME /tmp
EXPOSE 8101
ADD profiles-service.jar profiles-service.jar
#RUN bash -c 'touch /profiles-service.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom" ,"-jar", "/profiles-service.jar" ]
