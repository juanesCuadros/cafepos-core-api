# Stage 1: build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -q
COPY src src
# Sin -DskipTests a proposito: incluye ModularityTests, gate obligatorio
# antes de construir la imagen. Si un test falla, el build de Docker falla.
#
# Sin exclusion manual de tests: la convencion de sufijo (ver CLAUDE.md)
# ya resuelve esto sola. *Test = unitario, corre aca via surefire (mvn
# package). *IT = integracion real (necesita Postgres real, inalcanzable
# en este build aislado sin red hacia ningun servicio) — failsafe solo las
# corre en "mvn verify", nunca en "package", asi que ni siquiera se
# intentan aca. Sigue corriendo normal en una maquina de desarrollo con
# Postgres de docker-compose.yml levantado (mvn verify).
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
