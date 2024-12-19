FROM tomcat:10.1-jdk17

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/ChessEngine-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/

# Modify the server.xml to disable the shutdown port
RUN sed -i 's/<Server port="8005" shutdown="SHUTDOWN">/<Server port="-1" shutdown="SHUTDOWN">/' /usr/local/tomcat/conf/server.xml



