FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY build/libs/*.jar /app/myapp.jar

ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
