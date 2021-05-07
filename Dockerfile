FROM alpine:3.13

LABEL maintainer="VLINGO XOOM Team <info@vlingo.io>"

ENV JAVA_HOME=/usr/lib/jvm/default-jvm/
ENV PATH=${JAVA_HOME}/bin:$PATH
ENV XOOM_ENV="env"

ADD ./target/xoom-schemata-*-jar-with-dependencies.jar /schemata/xoom-schemata.jar

RUN addgroup -S xoom && adduser -S -D -s /sbin/nologin -h /schemata -G xoom xoom \
 && apk add --no-cache bash openjdk8 \
 && chown -R xoom:xoom /schemata

WORKDIR /schemata
CMD java -jar /schemata/xoom-schemata.jar $XOOM_ENV
USER xoom
