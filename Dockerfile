FROM tomcat:9.0
EXPOSE 5000
ADD target/myhome.war /usr/local/tomcat/webapps
CMD catalina.sh run