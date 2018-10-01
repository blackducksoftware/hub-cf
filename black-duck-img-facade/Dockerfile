FROM openjdk:8-jdk-alpine
ARG VERSION

COPY ./build/distributions/black-duck-img-facade-boot-${VERSION}.tar /black-duck-img-facade-boot-${VERSION}.tar
RUN mkdir -p /blackducksoftware/image-facade && tar xvf /black-duck-img-facade-boot-${VERSION}.tar -C /blackducksoftware/image-facade --strip-components=1 && rm /black-duck-img-facade-boot-${VERSION}.tar

CMD ["/blackducksoftware/image-facade/bin/black-duck-img-facade"]