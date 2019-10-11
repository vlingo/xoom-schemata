# First stage: Build application
FROM maven:3.6.2-jdk-8-slim as packager

## Allow docker to cache the maven dependencies in a separate layer for faster builds
## THIS IS CURRENTLY DEACTIVATED as otherwise stale (non -SNAPSHOT) dependencies will be cached.
# COPY pom.xml /home/project/pom.xml
# RUN cd /home/project && mvn dependency:go-offline -B

ADD . /home/project
RUN cd /home/project && mvn -B clean -Pfrontend package

# Second stage: Create runtime image
FROM openjdk:8-jdk-alpine

ENV JAVA_OPTS=""
ENV VLINGO_ENV="dev"

COPY --from=packager "/home/project/target/vlingo-schemata-*-jar-with-dependencies.jar" "/app.jar"

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar $VLINGO_ENV
