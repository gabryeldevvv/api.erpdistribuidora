# Use uma imagem base com o JDK 21 para o estágio de build
FROM openjdk:21-jdk-slim as builder

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Definir o diretório de trabalho dentro do container
WORKDIR /app

# Copiar os arquivos do projeto para o diretório de trabalho
COPY . /app

# Executar o build com Maven
RUN mvn clean package -DskipTests

# Segundo estágio: imagem final
FROM openjdk:21-jdk-slim

# Copiar o JAR do estágio de build
COPY --from=builder /app/target/api-0.0.1-SNAPSHOT.jar demo.jar

# Expor a porta
EXPOSE 8080

# Definir o comando de execução
ENTRYPOINT ["java", "-jar", "demo.jar"]