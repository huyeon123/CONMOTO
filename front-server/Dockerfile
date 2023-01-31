FROM openjdk:11-jre
COPY build/libs/*.jar app-front.jar
ENV CONMOTO_ENV="op" \
    ENCRYPT_KEY="super-space!5087" \
    CONFIG_SERVER="146.56.113.92:8888"
ENTRYPOINT ["java", "-jar", "app-front.jar"]
