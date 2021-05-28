FROM alpine:3.13 as jvm

LABEL maintainer="VLINGO XOOM Team <info@vlingo.io>"

ARG XOOM_HOME=/schemata
ENV JAVA_HOME=/usr/lib/jvm/default-jvm/
ENV PATH=${JAVA_HOME}/bin:$PATH
ENV XOOM_ENV="env"

RUN addgroup -S xoom && adduser -S -D -s /sbin/nologin -h $XOOM_HOME -G xoom xoom \
 && apk add --no-cache openjdk8

WORKDIR $XOOM_HOME

FROM jvm
USER xoom

COPY --chown=xoom:xoom ./target/xoom-schemata-*-jar-with-dependencies.jar $XOOM_HOME/xoom-schemata.jar
CMD java -jar xoom-schemata.jar $XOOM_ENV
