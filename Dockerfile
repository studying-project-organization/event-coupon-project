FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY gradlew /app/gradlew

COPY gradlew.bat /app/gradlew.bat

RUN chmod +x /app/gradlew

COPY build/libs/*.jar /app/myapp.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
