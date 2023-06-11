# Build stage
FROM maven:3.8.3-openjdk-17 AS build
COPY pom.xml /app/
RUN mvn verify --fail-never -DskipTests
COPY src /app/src
RUN mvn -f /app/pom.xml clean package -DskipTests

FROM tomcat:10
COPY --from=build /app/target/budget.war /usr/local/tomcat/webapps/
