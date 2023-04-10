FROM adoptopenjdk:17-jre-hotspot
WORKDIR /app

COPY target/java-exercise-1.0.0-SNAPSHOT.jar .

CMD ["java", "-jar", "java-exercise-1.0.0-SNAPSHOT.jar"]