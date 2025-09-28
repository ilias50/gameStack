# Étape 1 : Build
# Utilise une image OpenJDK pour compiler le projet
FROM openjdk:21 AS build

ARG JAR_FILE

# Définit le répertoire de travail
WORKDIR /app

# Copie le code source et les fichiers de build Maven
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src ./src

# Rend le script Maven exécutable et lance la compilation
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Étape 2 : Runtime
# Utilise une image plus petite pour l'exécution (contient seulement le JRE)
FROM openjdk:21

# Copie le fichier JAR compilé depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Lance l'application
ENTRYPOINT ["java", "-jar", "/app.jar"]