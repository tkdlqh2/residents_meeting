FROM eclipse-temurin:17-jdk-alpine
COPY build/libs/residents_meeting-1.0.jar /app.jar
ENV	USE_PROFILE compose

ENTRYPOINT ["java","-Dspring.profiles.active=${USE_PROFILE}","-jar","/app.jar"]