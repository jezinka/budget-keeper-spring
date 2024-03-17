# Build stage
FROM gradle:7.3.3-jdk17 AS build
WORKDIR /app
COPY settings.gradle /app/
COPY gradle /app/gradle
COPY api /app/api
COPY gui /app/gui
RUN gradle gui:npmInstall
RUN gradle api:processResources
RUN gradle -p /app/api war

# Run stage
FROM tomcat:10
COPY --from=build /app/api/build/libs/*.war /usr/local/tomcat/webapps/