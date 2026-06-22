#ETAPA DE CONSTRUCCIÓN
FROM gradle:8.10-jdk21-alpine AS build

WORKDIR /app

# Copiar solo los archivos de Gradle primero para cachear dependencias
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon

# Copiar el código fuente y construir
COPY src ./src
RUN gradle build --no-daemon -x test

# ===== ETAPA DE EJECUCIÓN =====
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copiar el JAR construido
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]