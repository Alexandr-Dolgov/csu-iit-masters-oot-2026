# syntax=docker/dockerfile:1

FROM amazoncorretto:21 AS build
WORKDIR /src
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY class01base/pom.xml class01base/
COPY class01OrderFlow/pom.xml class01OrderFlow/
COPY class02base/pom.xml class02base/
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q dependency:go-offline
COPY . .
ARG MODULE=class01base
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q -pl ${MODULE} -am package -DskipTests \
    && cp ${MODULE}/target/${MODULE}-*.jar /app.jar

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app.jar app.jar
ARG MAIN_CLASS=ru.csu.iit.master2026.oot.class01base.Main
ENV MAIN_CLASS=${MAIN_CLASS}
ENTRYPOINT ["sh", "-c", "exec java -cp app.jar \"$MAIN_CLASS\" \"$@\"", "--"]
