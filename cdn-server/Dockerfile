FROM openjdk:11-jre
COPY build/libs/*.jar app-cdn.jar
ARG ENCRYPT_KEY
ARG CONFIG_SERVER
ENV CONMOTO_ENV="op" \
    ENCRYPT_KEY=$ENCRYPT_KEY \
    CONFIG_SERVER=$CONFIG_SERVER
ENTRYPOINT ["java", "-jar", "app-cdn.jar"]
