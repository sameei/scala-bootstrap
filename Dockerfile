
FROM openjdk:8
RUN ["mkdir", "-p", "/prod/application"]
COPY ./target/universal/stage /prod/application
EXPOSE 4050
ENTRYPOINT ["/prod/application/bin/scala-bootstrap"]
