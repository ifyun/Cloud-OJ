FROM openjdk:11-jre-alpine
COPY ./target/*.jar /app/gateway.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "${JVM_OPTS}", "-Dspring.profiles.active=prod", "-jar", "gateway.jar"]