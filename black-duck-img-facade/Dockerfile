FROM openjdk:8-jdk-alpine
ARG VERSION

COPY ./build/distributions/black-duck-img-facade-boot-${VERSION}.tar /black-duck-img-facade-boot-${VERSION}.tar
COPY ./docker-entrypoint.sh /usr/local/bin/.
RUN set -e \
    \
    && mkdir -p /blackducksoftware/image-facade \
    && tar xvf /black-duck-img-facade-boot-${VERSION}.tar -C /blackducksoftware/image-facade --strip-components=1 \
    && rm /black-duck-img-facade-boot-${VERSION}.tar \
    \
    && chmod +x /usr/local/bin/docker-entrypoint.sh
    
ENV BLACK_DUCK_IMG_FACADE_OPTS -Djavax.net.ssl.trustStore=/blackducksoftware/image-facade/security/truststore \
    -Djava.security.egd=file:/dev/./urandom

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["/blackducksoftware/image-facade/bin/black-duck-img-facade"]