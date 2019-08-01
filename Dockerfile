FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENV EXPIRATION="60000"
ENTRYPOINT [ "sh", "-c", "java -DexpirationTimeMillis=${EXPIRATION} -jar /app.jar" ]