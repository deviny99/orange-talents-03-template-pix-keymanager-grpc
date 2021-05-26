FROM openjdk:11.0.11-jre
MAINTAINER Marcos Rocha <nyrocha2010@hotmail.com>
COPY build/libs/key-manager-0.1-all.jar /etc/keymanager-GRPC.jar
WORKDIR /etc
ENV PORT_GRPC=50051
CMD ["java", "-jar", "keymanager-GRPC.jar"]
EXPOSE 50051