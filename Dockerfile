# Build stage (Java 21 + Maven)
FROM eclipse-temurin:21-jdk-jammy as builder

WORKDIR /app
# Copia todos os arquivos necessários para o Maven Wrapper
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src src

# Dá permissão de execução ao mvnw
RUN chmod +x mvnw

# Cache das dependências Maven
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline

# Build da aplicação
RUN ./mvnw clean package -DskipTests

# Runtime stage (mesmo que antes)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]