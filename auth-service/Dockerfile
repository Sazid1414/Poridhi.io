FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/auth-service-0.0.1-SNAPSHOT.jar auth-service.jar
ENTRYPOINT ["java","-jar","/auth-service.jar"]