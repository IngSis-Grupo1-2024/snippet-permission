FROM gradle:8.7.0-jdk17-jammy AS build
COPY  . /app
WORKDIR /app
RUN ./gradlew bootJar

FROM amazoncorretto:17-alpine
EXPOSE 8080
RUN mkdir /app
COPY --from=build /app/build/libs/SnippetPermission.jar /app/SnippetPermission.jar
COPY newrelic/newrelic.jar /app/newrelic.jar
COPY newrelic/newrelic.yml /app/newrelic.yml
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production","-javaagent:/app/newrelic.jar","/app/SnippetPermission.jar"]