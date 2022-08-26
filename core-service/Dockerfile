FROM openjdk:11-jre-alpine
COPY ./target/*.jar /app/core-service.jar
WORKDIR /app
EXPOSE 8081
CMD ["java", "${JVM_OPTS}", "-Dspring.profiles.active=prod", "-jar", "core-service.jar"]