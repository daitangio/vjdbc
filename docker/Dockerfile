FROM alpine/git as clone
WORKDIR /app
RUN git clone https://github.com/mrloud/vjdbc.git

FROM maven:3.6.3-jdk-11-slim
WORKDIR /app
COPY --from=clone /app/vjdbc /app 
RUN mvn install 

FROM openjdk:11-jre-slim-buster as build

WORKDIR /app
RUN mkdir /conf
VOLUME /conf

COPY --from=build /app/target/vjdbc-3.0.0.jar /app
COPY --from=build /app/target/lib/* /app/lib/ 
COPY vjdbc_jdbc.xml /conf

CMD ["java","-Djava.rmi.server.hostname=0.0.0.0","-cp",".:./*:./lib/*:/conf/*","de.simplicit.vjdbc.server.rmi.ConnectionServer","/conf/vjdbc_jdbc.xml"]