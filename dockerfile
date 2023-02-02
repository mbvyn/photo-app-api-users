FROM openjdk:17-jdk-slim
VOLUME /tmp 
COPY target/photo-app-api-users-0.0.1-SNAPSHOT.jar UsersMicroservice.jar 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/UsersMicroservice.jar"] 