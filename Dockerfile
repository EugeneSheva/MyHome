FROM tomcat:9-jdk11-temurin-focal
EXPOSE 8080
ADD target/myhome.war /usr/local/tomcat/webapps/
CMD catalina.sh run
