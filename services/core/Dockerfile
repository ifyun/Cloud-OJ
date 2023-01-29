FROM openjdk:17-slim-bullseye
COPY ./target/lib/* /app/lib/
COPY ./target/*.jar /app/core.jar
WORKDIR /app
EXPOSE 8081
CMD ["java", "${JVM_OPTS}", "-Dspring.profiles.active=prod", "-jar", "core.jar"]