FROM tomcat:9-jdk11-temurin-focal
EXPOSE 8080
ADD target/myhome.war /usr/local/tomcat/webapps/
#VOLUME /home/avada/web/slj.avada-media-dev1.od.ua/slj/MyHome24
CMD catalina.sh run
