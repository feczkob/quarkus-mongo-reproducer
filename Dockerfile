# build
FROM openjdk:21-jdk-slim AS build

ENV APP_HOME=/usr/app

RUN apt-get update && \
    apt-get install -y curl && \
    apt-get clean && \
    mkdir -p "$APP_HOME"
COPY . $APP_HOME

WORKDIR $APP_HOME/menetrendek-api
RUN ./mvnw clean install -q

WORKDIR $APP_HOME/nbt-planner-ab-api
RUN ./mvnw clean install -q

WORKDIR $APP_HOME
RUN ./mvnw clean install -q && \
    ./mvnw test-compile failsafe:integration-test failsafe:verify -Dnative -q && \
    ./mvnw org.hjug.refactorfirst.plugin:refactor-first-maven-plugin:0.5.0:simpleHtmlReport -q


# base image to build a JRE
FROM amazoncorretto:21-alpine AS corretto-jdk

# required for strip-debug to work & build small JRE image
RUN apk add --no-cache binutils && \
    "$JAVA_HOME"/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /customjre


# main app image
FROM alpine:3.20.3 AS main-image
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV APP_HOME=/usr/app
ENV DEPLOYMENTS_HOME=/deployments

# copy JRE from the base image
COPY --from=corretto-jdk /customjre $JAVA_HOME

# add app user & configure working directory
ARG APPLICATION_USER=appuser
RUN adduser --no-create-home -u 1000 -D "$APPLICATION_USER" && \
    mkdir "$DEPLOYMENTS_HOME" && \
    chown -R "$APPLICATION_USER" "$DEPLOYMENTS_HOME"

USER 1000

COPY --chown=1000 --from=build $APP_HOME/target/quarkus-app/lib/ "$DEPLOYMENTS_HOME"/lib/
COPY --chown=1000 --from=build $APP_HOME/target/quarkus-app/*.jar "$DEPLOYMENTS_HOME"/
COPY --chown=1000 --from=build $APP_HOME/target/quarkus-app/app/ "$DEPLOYMENTS_HOME"/app/
COPY --chown=1000 --from=build $APP_HOME/target/quarkus-app/quarkus/ "$DEPLOYMENTS_HOME"/quarkus/
COPY --chown=1000 --from=build $APP_HOME/target/site/ "$DEPLOYMENTS_HOME"/report/
WORKDIR "$DEPLOYMENTS_HOME"

EXPOSE 8080
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENTRYPOINT [ "/jre/bin/java", "-jar", "/deployments/quarkus-run.jar" ]

# 1. ./mvnw clean install
# 2. docker build -f src/main/docker/simple/Dockerfile.jvm -t nbt-planner/nbt-planner-ab:trial .
# 3. docker run -i --rm -p 8080:8080 --network nbt-planner --name nbt-planner-ab_app_1 nbt-planner/nbt-planner-ab:trial

# docker run -it --entrypoint sh --rm -p 8080:8080 --network nbt-planner nbt-planner/nbt-planner-ab:custom-jre

# See https://blog.monosoul.dev/2022/04/25/reduce-java-docker-image-size/
