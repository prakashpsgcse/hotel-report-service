FROM openjdk:8

ARG API_JAR_NAME
ARG PROJECT_JAR_VERSION
ADD target/${API_JAR_NAME}-${PROJECT_JAR_VERSION}.jar hotel-resource-api.jar
expose 8080
ENTRYPOINT ["java","-jar","hotel-resource-api.jar"]