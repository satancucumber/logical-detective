FROM maven:3.8.5-openjdk-17-slim as builder
WORKDIR /app
COPY . /app/.
RUN mvn -f pom.xml -Dmaven.test.skip clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/*.jar
EXPOSE 8181
ENTRYPOINT ["java", "-jar", "/app/*.jar"]