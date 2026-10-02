FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/patent-disclosure-workflow-1.0.0.war app.war

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.war"]