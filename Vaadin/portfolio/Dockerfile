# syntax=docker/dockerfile:1
# Node 24 is required for Vaadin 25 frontend asset generation.
FROM node:24-bookworm-slim AS node
FROM maven:3.9.11-eclipse-temurin-21 AS build
COPY --from=node /usr/local/bin/node /usr/local/bin/node
COPY --from=node /usr/local/lib/node_modules /usr/local/lib/node_modules
RUN ln -s /usr/local/lib/node_modules/npm/bin/npm-cli.js /usr/local/bin/npm \
    && ln -s /usr/local/lib/node_modules/npm/bin/npx-cli.js /usr/local/bin/npx
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -ntp -Pproduction -DskipTests clean package \
    && cp target/vaadin-portfolio-1.0.0.jar /workspace/app.jar

FROM eclipse-temurin:21-jre AS runtime
RUN groupadd --system portfolio && useradd --system --gid portfolio --home-dir /opt/portfolio portfolio
WORKDIR /opt/portfolio
COPY --from=build --chown=portfolio:portfolio /workspace/app.jar app.jar
USER portfolio
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -Dfile.encoding=UTF-8"
ENTRYPOINT ["java", "-jar", "/opt/portfolio/app.jar"]
