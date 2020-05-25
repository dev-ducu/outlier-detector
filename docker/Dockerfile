FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/outlier-detector-1.0.0.jar
COPY ${JAR_FILE} outlier-detector.jar
ENV TZ=Europe/Bucharest
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT ["java","-jar","/outlier-detector.jar"]
