
FROM sbtscala/scala-sbt

RUN mkdir /build
WORKDIR /build

COPY . /build

RUN sbt bt
