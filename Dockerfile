# Build stage
FROM maven:3 AS build
COPY pom.xml /app/
RUN mvn verify --fail-never
COPY src /app/src
RUN mvn -f /app/pom.xml clean package

FROM tomcat:9
COPY --from=build /app/target/budget.war /usr/local/tomcat/webapps/
