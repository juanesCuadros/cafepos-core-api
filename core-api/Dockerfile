# Stage 1: build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw
RUN ./mvnw dependency:go-offline -q
COPY src src
ENV MAVEN_OPTS="-Xmx1024m -XX:+TieredCompilation -XX:TieredStopAtLevel=1"
RUN ./mvnw package -q

# Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S cafepos && adduser -S cafepos -G cafepos
WORKDIR /app
# Wildcard en vez de nombre fijo: no depende de la version exacta en pom.xml
COPY --from=builder /app/target/*.jar app.jar
RUN chown cafepos:cafepos app.jar
USER cafepos
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
