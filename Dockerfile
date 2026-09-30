FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/patent-disclosure-workflow-1.0.0.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
