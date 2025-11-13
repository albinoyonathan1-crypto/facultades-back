FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/facultades-0.0.1.jar
COPY ${JAR_FILE} app_facultades.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app_facultades.jar"]