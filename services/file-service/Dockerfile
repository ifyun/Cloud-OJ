FROM openjdk:17-slim-bullseye
COPY ./target/*.jar /app/file-service.jar
WORKDIR /app
EXPOSE 8082
CMD ["java", "${JVM_OPTS}", "-Dspring.profiles.active=prod", "-jar", "file-service.jar"]