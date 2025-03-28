FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar /app/myapp.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]