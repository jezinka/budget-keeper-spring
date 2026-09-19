# Build stage
FROM gradle:9.1.0-jdk21 AS build
WORKDIR /app
COPY settings.gradle /app/
COPY gradle /app/gradle
COPY api /app/api
COPY gui /app/gui
RUN gradle gui:npmInstall
RUN gradle api:processResources
RUN gradle -p /app/api war

# Run stage
FROM tomcat:10.1.19-jre17
COPY --from=build /app/api/build/libs/*.war /usr/local/tomcat/webapps/