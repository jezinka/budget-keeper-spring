# Build stage
FROM gradle:7.3.3-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle
COPY src /app/src
RUN gradle -p /app clean build -x test

# Run stage
FROM tomcat:10
COPY --from=build /app/build/libs/*.war /usr/local/tomcat/webapps/