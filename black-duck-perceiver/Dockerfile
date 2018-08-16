FROM centos:centos7
ARG VERSION

RUN yum install -y java-1.8.0-openjdk && yum clean all

ENV JAVA_HOME /etc/alternatives/jre

COPY ./perceiver.sh /perceiver.sh
COPY ./build/libs/black-duck-perceiver-${VERSION}.jar /opt/perceiver/perceiver.jar
RUN chmod 755 /perceiver.sh

CMD ["/perceiver.sh"]