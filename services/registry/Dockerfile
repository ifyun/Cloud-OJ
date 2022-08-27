FROM openjdk:11-jre-alpine
COPY ./target/*.jar /app/registry.jar
WORKDIR /app
EXPOSE 8761
CMD ["java", "${JVM_OPTS}", "-Dspring.profiles.active=prod", "-jar", "registry.jar"]