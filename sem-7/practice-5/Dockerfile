FROM gradle:jdk17 AS build

WORKDIR /app

ADD settings.gradle.kts gradlew ./
RUN gradle wrapper
ADD build.gradle.kts ./
RUN ./gradlew build || return 0 # download dependencies, fail at build

ADD src src
RUN ./gradlew clean build -x test

FROM openjdk:17-slim
WORKDIR /app

COPY --from=build /app/build/libs/practice-5.jar ./

EXPOSE 80

CMD [ "java", "-Dspring.datasource.url=jdbc:postgresql://host.docker.internal:5432/postgres", "-jar", "practice-5.jar"]
