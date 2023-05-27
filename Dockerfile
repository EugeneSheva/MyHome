FROM eclipse-temurin:11
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve
COPY src ./src
CMD ["./mvnw", "clean package -DskipTests"]

FROM tomcat:9.0
EXPOSE 8080
ADD target/myhome.war /usr/local/tomcat/webapps/
CMD catalina.sh run
