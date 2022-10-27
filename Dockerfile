FROM openjdk:8-jre-alpine
RUN mkdir -p /sidekick-test-mode
ARG JAR_FILE
ADD ${JAR_FILE} sidekick-test-mode/
WORKDIR sidekick-test-mode/
EXPOSE 8084
ENTRYPOINT java -jar sidekick-test-mode.jar
