FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY build/libs/*.jar /app/myapp.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
