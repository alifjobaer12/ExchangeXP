# Build stage
FROM eclipse-temurin:8-jdk AS build
WORKDIR /src
COPY . .
RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests package



FROM eclipse-temurin:8-jdk
WORKDIR /app
COPY --from=build /src/target/*.jar /app/app.jar
COPY exchangexp-run.sh /exchangexp-run.sh
RUN chmod +x /exchangexp-run.sh
EXPOSE 8080
ENTRYPOINT ["/exchangexp-run.sh"]
