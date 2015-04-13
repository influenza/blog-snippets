# Start with a nearly perfect base
FROM library/tomcat
ADD tomcat-users.xml /usr/local/tomcat/conf/
ADD context.xml /usr/local/tomcat/conf/
ADD https://jdbc.postgresql.org/download/postgresql-9.4-1201.jdbc41.jar /usr/local/tomcat/lib/postgresql.jar
VOLUME ["/usr/local/tomcat/webapps/"]
