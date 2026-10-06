# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Descarga dependencias primero (se cachean mientras no cambie el pom.xml)
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src src
RUN mvn clean package -DskipTests -B

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Zona horaria de Peru
ENV TZ=America/Lima

COPY --from=build /app/target/*.jar backend.jar

# Carpeta donde se guardan las fotos de los reportes (app.upload.dir)
RUN mkdir -p /app/uploads/reportes

# TrashTracker corre en el puerto 8088 (server.port en application.properties)
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "backend.jar"]