FROM java:8
VOLUME /tmp
ADD technical-test-1.0.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 8000
ENTRYPOINT ["java","-jar","/app.jar"]