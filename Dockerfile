FROM adoptopenjdk/openjdk11:alpine-jre

EXPOSE 8080

ADD target/cloud_service-0.0.1-SNAPSHOT.jar cloud_service_docker.jar

ENTRYPOINT ["java", "-jar", "/cloud_service_docker.jar" ]