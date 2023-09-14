FROM openjdk:11-jre-slim
COPY ./target/intelliservice-agentanalytics-0.0.1-SNAPSHOT.jar /usr/local/lib/app.jar
EXPOSE 9909

# Create a group and user
#RUN addgroup -S itsgroup && adduser -S itsuser -G itsgroup
RUN adduser --system --group itsuser
RUN chmod -R 777 /usr/local
# Tell docker that all future commands should run as the itsuser user
USER itsuser

ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
