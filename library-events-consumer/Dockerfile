ARG BUILD_IMAGE=maven:3.9.5-eclipse-temurin-17-focal
ARG RUNTIME_IMAGE=openjdk:22-ea-17-slim

FROM ${BUILD_IMAGE} as dependencies
ENV APP_HOME=/
WORKDIR $APP_HOME

COPY pom.xml $APP_HOME
ADD src $APP_HOME/src
RUN mvn -f pom.xml clean package install

FROM ${RUNTIME_IMAGE}
ENV ARTIFACT_NAME=*.jar
ENV PROFILE=docker

EXPOSE 8091

COPY --from=dependencies /target/$ARTIFACT_NAME /app/app.jar

CMD ["java", "-Dspring.profiles.active=${PROFILE}","-Duser.timezone=America/Lima", "-Duser.language=es", "-Duser.country=PE", "-jar", "/app/app.jar"]