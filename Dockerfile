FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY gradlew /app/gradlew
COPY gradlew.bat /app/gradlew.bat
COPY gradle /app/gradle

RUN chmod +x /app/gradlew

COPY build/libs/*.jar /app/myapp.jar

ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
