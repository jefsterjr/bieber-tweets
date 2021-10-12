FROM openjdk:11
MAINTAINER Jefster Farlei <jefsterfarlei@gmail.com>

RUN mkdir /usr/app

WORKDIR /java-exercise
COPY target/java-exercise-1.0.0.jar .

ENTRYPOINT ["java","-jar","java-exercise-1.0.0.jar"]
