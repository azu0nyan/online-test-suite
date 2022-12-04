
FROM sbtscala/scala-sbt:latest

RUN mkdir /build
WORKDIR /build

COPY . /build

RUN sbt bt