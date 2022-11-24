FROM openjdk:11-jdk-slim
RUN mkdir -p /sidekick-test-mode
ARG JAR_FILE
ADD ${JAR_FILE} sidekick-test-mode/
WORKDIR sidekick-test-mode/
EXPOSE 80
ENTRYPOINT java -jar sidekick-test-mode.jar
