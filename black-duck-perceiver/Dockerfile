FROM openjdk:8-jdk-alpine
ARG VERSION

# RUN yum install -y java-1.8.0-openjdk && yum clean all

# ENV JAVA_HOME /etc/alternatives/jre

COPY ./build/distributions/black-duck-perceiver-boot-${VERSION}.tar /black-duck-perceiver-boot-${VERSION}.tar
RUN mkdir -p /blackducksoftware/perceiver && tar xvf /black-duck-perceiver-boot-${VERSION}.tar -C /blackducksoftware/perceiver --strip-components=1 && rm /black-duck-perceiver-boot-${VERSION}.tar

CMD ["/blackducksoftware/perceiver/bin/black-duck-perceiver"]