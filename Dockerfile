FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew assemble
EXPOSE 8080
CMD ["java", "-jar", "/app/build/libs/API_Gateway-0.0.1-SNAPSHOT.jar"]



