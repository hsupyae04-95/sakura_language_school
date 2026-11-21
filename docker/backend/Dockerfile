FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY language-school/src language-school/src
COPY language-school/pom.xml language-school/pom.xml
RUN mvn -B -f language-school/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy AS runtime

WORKDIR /app

RUN apt-get update \
    && apt-get install --yes --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --create-home --uid 10001 appuser
COPY --from=build /workspace/language-school/target/language-school-0.0.1-SNAPSHOT.jar /app/app.jar

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
