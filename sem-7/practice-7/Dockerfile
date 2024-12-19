FROM gradle:jdk17 AS build

WORKDIR /app

ADD settings.gradle gradlew ./
RUN gradle wrapper
ADD build.gradle ./
RUN ./gradlew build || return 0 # download dependencies, fail at build

ADD src src
RUN ./gradlew clean build -x test

FROM openjdk:17-slim
WORKDIR /app

COPY --from=build /app/build/libs/practice-7.jar ./

EXPOSE 80

CMD [ "java", "-jar", "practice-7.jar"]