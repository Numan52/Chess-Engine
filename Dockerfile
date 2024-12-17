FROM tomcat:10.1-jdk17

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/ChessEngine-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/



