FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    xvfb libgtk-3-0 libxtst6 libxrender1 \
    libxi6 libgl1 libasound2t64 && \
    rm -rf /var/lib/apt/lists/*

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

CMD ["sh", "-c", "xvfb-run -a mvn javafx:run"]