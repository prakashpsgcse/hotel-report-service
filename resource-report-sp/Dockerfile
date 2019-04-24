FROM openjdk:8

ARG API_JAR_NAME
ARG PROJECT_JAR_VERSION
ADD target/${API_JAR_NAME}-${PROJECT_JAR_VERSION}.jar hotel-resource-sp.jar
ENTRYPOINT ["java","-jar","hotel-resource-sp.jar"]