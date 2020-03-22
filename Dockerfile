FROM openjdk:8-jdk-alpine

ENV JAVA_OPTS=""
ENV VLINGO_ENV="env"

ADD ./target/vlingo-schemata-*-jar-with-dependencies.jar /app.jar

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar $VLINGO_ENV
