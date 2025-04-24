FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/search-service-0.0.1-SNAPSHOT.jar search-service.jar
ENTRYPOINT ["java","-jar","/search-service.jar"]