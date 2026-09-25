# --- build ---
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /src
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -q dependency:go-offline
COPY src/ src/
RUN ./mvnw -B -q package -DskipTests \
 && java -Djarmode=tools -jar target/status-page-*.jar extract --layers --launcher --destination /extracted

# --- runtime ---
FROM eclipse-temurin:25.0.4_7-jre-alpine
RUN addgroup -S app && adduser -S -G app app \
 && mkdir /data && chown app:app /data
WORKDIR /app
COPY --from=build /extracted/dependencies/ ./
COPY --from=build /extracted/spring-boot-loader/ ./
COPY --from=build /extracted/snapshot-dependencies/ ./
COPY --from=build /extracted/application/ ./
VOLUME /data
USER app
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/actuator/health || exit 1
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
