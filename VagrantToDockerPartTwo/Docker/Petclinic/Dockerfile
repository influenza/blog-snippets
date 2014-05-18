FROM rouge8/precise-openjdk-7-jdk

# Update the cache and our installed packages
RUN echo "deb http://archive.ubuntu.com/ubuntu precise main universe" > /etc/apt/sources.list
RUN apt-get update && apt-get upgrade -y
# Install application requirements
RUN apt-get install -y git
# Retrieve the code
RUN mkdir /opt/petclinic
RUN git clone https://github.com/grails-samples/grails-petclinic.git /opt/petclinic/app
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64/
# Change the data source to reference a linked 'database' container
ENV PETCLINIC_DB database
# Ready the grails installation
RUN cd /opt/petclinic/app ; echo `./grailsw test-app`
WORKDIR /opt/petclinic/app/
# Expose the grails web-app port
EXPOSE 8080
# Run the application when running the container with no params
ENTRYPOINT ["bash"]
