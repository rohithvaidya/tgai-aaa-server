ARG VERSION="0.0.1-SNAPSHOT"

# Using maven base image in builder stage to build Java code.
FROM maven:3-eclipse-temurin-21 as builder

WORKDIR /usr/share/app
COPY pom.xml .

# Downloads all packages defined in pom.xml
RUN mvn clean package
COPY src src

# Build the source code to generate the fatjar
RUN mvn clean package -Dmaven.test.skip=true

# Java Runtime as the base for final image
FROM eclipse-temurin:21-jre

ARG VERSION
ENV JAR="iudx.aaa.server-dev-${VERSION}-fat.jar"

WORKDIR /usr/share/app

# Copying openapi docs
COPY docs docs

# Copying dev fatjar from builder stage to final image
COPY --from=builder /usr/share/app/target/${JAR} ./fatjar.jar

EXPOSE 8080 8443

# Creating a non-root user
RUN useradd -r -u 1001 -g root aaa-user

# Setting non-root user to use when container starts
USER aaa-user
