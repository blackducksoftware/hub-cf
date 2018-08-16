FROM centos:centos7
ARG VERSION

RUN yum install -y java-1.8.0-openjdk && yum clean all

ENV JAVA_HOME /etc/alternatives/jre

COPY ./image-facade.sh /image-facade.sh
COPY ./build/libs/black-duck-img-facade-${VERSION}.jar /opt/image-facade/image-facade.jar
RUN chmod 755 /image-facade.sh

CMD ["/image-facade.sh"]