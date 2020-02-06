FROM openjdk:8
VOLUME /tmp
EXPOSE 8023
ADD ./target/spring-boot-webflu-ms-op-banco-0.0.1-SNAPSHOT.jar ms.op.banco.jar
ENTRYPOINT ["java","-jar","/ms.op.banco.jar"]