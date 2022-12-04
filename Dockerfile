
FROM sbtscala/scala-sbt:openjdk-17.0.2_1.8.0_3.2.1

RUN mkdir /build
WORKDIR /build

COPY . /build

RUN sbt bt
