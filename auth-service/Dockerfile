FROM openjdk:21-jdk-alpine
EXPOSE 8002
COPY target/auth-service-0.0.1-SNAPSHOT.jar auth-service.jar
ENTRYPOINT ["java","-jar","/auth-service.jar"]