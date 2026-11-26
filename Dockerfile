FROM gradle:8.14.3-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*-all.jar app.jar
ENV PORT=8080
EXPOSE 8080
USER 10001
ENTRYPOINT ["java", "-jar", "app.jar"]
