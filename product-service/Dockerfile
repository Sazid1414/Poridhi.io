FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/product-service-0.0.1-SNAPSHOT.jar product-service.jar
ENTRYPOINT ["java","-jar","/product-service.jar"]