FROM gradle:7.6.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build -x test

FROM openjdk:17-jdk-slim
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/crypto-tracker-bot.jar
CMD ["java", "-jar", "/app/crypto-tracker-bot.jar"]