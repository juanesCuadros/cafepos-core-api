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
# Excepcion puntual: PermisoEvaluatorIntegrationTest necesita un Postgres real
# ya poblado (tenant de prueba + catalogo de permisos sembrado en
# localhost:5434), inalcanzable dentro de este build aislado sin red hacia
# ningun servicio. No es un test que deba correr aca — sigue corriendo normal
# en una maquina de desarrollo con el Postgres de docker-compose.yml levantado
# (mvn test, sin exclusion). Unica prueba excluida; ModularityTests y el resto
# del gate siguen obligatorios.
RUN ./mvnw package -q -Dtest='!PermisoEvaluatorIntegrationTest'

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
