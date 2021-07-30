FROM openjdk:8-alpine

COPY target/uberjar/hs.jar /hs/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/hs/app.jar"]
