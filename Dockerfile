FROM openjdk:8-jdk-alpine

ENV JAVA_OPTS=""
ENV XOOM_ENV="env"

ADD ./target/xoom-schemata-*-jar-with-dependencies.jar /app.jar

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar $XOOM_ENV
