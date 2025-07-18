FROM tomcat:10-jdk21
COPY ガチャ.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
