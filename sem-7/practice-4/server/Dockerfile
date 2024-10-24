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

COPY --from=build /app/build/libs/server.jar ./

EXPOSE 8080
EXPOSE 5200

ENTRYPOINT ["java", "-jar"]
CMD ["server.jar"]