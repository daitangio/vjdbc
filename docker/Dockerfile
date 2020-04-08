FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/mrloud/vjdbc.git

FROM maven:3.6.3-jdk-11-slim
WORKDIR /app
COPY --from=0 /app/vjdbc /app 
RUN mvn install 

FROM adoptopenjdk:11.0.6_10-jdk-hotspot-bionic

WORKDIR /app
RUN mkdir /conf
VOLUME /conf

COPY --from=1 /app/target/vjdbc-2.0.jar /app
COPY --from=1 /app/target/lib/* /app/lib/ 
COPY log4j.xml /app
COPY vjdbc_jdbc.xml /conf

CMD ["java","-Djava.rmi.server.hostname=0.0.0.0","-cp",".:./*:./lib/*:/conf/*","de.simplicit.vjdbc.server.rmi.ConnectionServer","/conf/vjdbc_jdbc.xml"]