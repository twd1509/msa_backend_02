FROM --platform=linux/amd64/v3 openjdk:21	
ARG JAR_FILE=./aianalysis-0.0.1-SNAPSHOT.jar 	
COPY ${JAR_FILE} aianalysis-0.0.1-SNAPSHOT.jar 	
ENTRYPOINT ["java","-jar","/aianalysis-0.0.1-SNAPSHOT.jar"]