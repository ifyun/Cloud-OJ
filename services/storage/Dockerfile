FROM openjdk:17-slim-bullseye
COPY ./target/lib/* /app/lib/
COPY ./target/*.jar /app/storage.jar
WORKDIR /app
EXPOSE 8082
CMD ["java", "${JVM_OPTS}", "-Dspring.profiles.active=prod", "-jar", "storage.jar"]