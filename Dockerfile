FROM ubuntu:latest as BUILDER

RUN cd / && \
    apt update && apt install -y wget curl build-essential libz-dev zlib1g-dev && apt upgrade -y && \
    curl -sSL "https://gist.githubusercontent.com/ottx96/1742e390d1785941b2ae9a8f46c22d84/raw/c77f29fc8fdd17d894c26617751c7df8f81bfbce/download-latest-graal-vm.sh" | sh -  && \
    tar xzvf graalvm-ce.tar.gz && rm graalvm-ce.tar.gz && \
    for i in $(ls -1d graalvm-*); do mv "$i" "/graalvm-ce/"; done && \
    export PATH="$PATH:/graalvm-ce/bin" && \
    gu install native-image

WORKDIR /home/app

COPY build/layers/libs /home/app/libs
COPY build/layers/resources /home/app/resources
COPY build/layers/application.jar /home/app/application.jar

RUN export PATH="$PATH:/graalvm-ce/bin" && \
    native-image -cp /home/app/libs/*.jar:/home/app/resources:/home/app/application.jar com.github.ottx96.Entrypoint

FROM frolvlad/alpine-glibc
RUN apk update && apk add libstdc++
COPY --from=BUILDER /home/app/application /app/application
ENTRYPOINT ["/app/application"]