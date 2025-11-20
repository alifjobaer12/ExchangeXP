# Build stage
FROM eclipse-temurin:8-jdk AS build
WORKDIR /src
COPY . .
RUN ./mvnw -B -DskipTests package



FROM eclipse-temurin:8-jdk
WORKDIR /app
COPY --from=build /src/target/*.jar /app/app.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
