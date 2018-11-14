FROM openjdk:8-jdk-alpine
ARG VERSION

COPY ./build/distributions/black-duck-perceiver-${VERSION}-boot.tar /black-duck-perceiver-${VERSION}-boot.tar
COPY ./docker-entrypoint.sh /usr/local/bin/.
RUN set -e \
    \
    && mkdir -p /blackducksoftware/perceiver \
    && tar xvf /black-duck-perceiver-${VERSION}-boot.tar -C /blackducksoftware/perceiver --strip-components=1 \
    && rm /black-duck-perceiver-${VERSION}-boot.tar \
    \
    && chmod +x /usr/local/bin/docker-entrypoint.sh
    
ENV BLACK_DUCK_PERCEIVER_OPTS -Djavax.net.ssl.trustStore=/blackducksoftware/perceiver/security/truststore \
    -Dlogging.level.root=DEBUG \
    -Djava.security.egd=file:/dev/./urandom

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["/blackducksoftware/perceiver/bin/black-duck-perceiver"]
