# starlingtechtest
Pankaj Pardasani

## Running the service

###Using spring-boot mvn plugin
mvn clean spring-boot:run -f pom.xml

###From command line using java program along with embedded tomcat container
java -classpath <all jar files seperated by semicolon> com.starlingbank.tech.api.roundup.SaveTheChangeAPITest -Dspring.config.location=classpath:/application.yaml