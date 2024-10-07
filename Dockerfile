FROM maven:3.9.9 AS builder

WORKDIR /app

ADD . /app

RUN cd /app && mvn clean install

###################################################

FROM openjdk:22-jdk

WORKDIR /app

COPY --from=builder /app/hexagonal-adapter-jetty/target/hexagonal-adapter-jetty-*-jar-with-dependencies.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
