FROM openjdk:8-jdk-alpine

# RUN addgroup -S app && adduser -S -g app app

ENV GRADLE_HOME /opt/gradle
ENV GRADLE_VERSION 4.4

ARG GRADLE_DOWNLOAD_SHA256=fa4873ae2c7f5e8c02ec6948ba95848cedced6134772a0169718eadcb39e0a2f
RUN set -o errexit -o nounset \
	&& echo "Installing build dependencies" \
	&& apk add --no-cache --virtual .build-deps \
		ca-certificates \
		openssl \
		unzip \
	\
	&& echo "Downloading Gradle" \
	&& wget -O gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
	\
	&& echo "Checking download hash" \
	&& echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum -c - \
	\
	&& echo "Installing Gradle" \
	&& unzip gradle.zip \
	&& rm gradle.zip \
	&& mkdir /opt \
	&& mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
	&& ln -s "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
	\
	&& apk del .build-deps \
	\
	&& echo "Adding gradle user and group" \
	&& addgroup -S -g 1000 gradle \
	&& adduser -D -S -G gradle -u 1000 -s /bin/ash gradle \
	&& mkdir /home/gradle/.gradle \
	&& chown -R gradle:gradle /home/gradle \
	\
	&& echo "Symlinking root Gradle cache to gradle Gradle cache" \
	&& ln -s /home/gradle/.gradle /root/.gradle


ENV KOTLIN_HOME /opt/kotlinc
ENV KOTLIN_VERSION 1.2.10

RUN set -o errexit -o nounset \
    && echo "Downloading Kotlin" \
    && apk --no-cache add bash ca-certificates wget curl \
    && wget --no-verbose --output-document=/tmp/kotlin-compiler.zip "https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip" \
    && mkdir -p /opt \
    && unzip -d /opt /tmp/kotlin-compiler.zip \
    && rm /tmp/kotlin-compiler.zip \
    && echo "Pulling watchdog binary from Github." \
    && curl -sSL https://github.com/openfaas/faas/releases/download/0.6.12/fwatchdog > /usr/bin/fwatchdog \
    && chmod +x /usr/bin/fwatchdog \
    && apk del curl --no-cache

RUN ln -s "${KOTLIN_HOME}/bin/kotlin" /usr/bin/kotlin \
    && ln -s "${KOTLIN_HOME}/bin/kotlinc" /usr/bin/kotlinc \
    && ln -s "${KOTLIN_HOME}/bin/kotlinc-js" /usr/bin/kotlinc-js \
    && ln -s "${KOTLIN_HOME}/bin/kotlinc-jvm" /usr/bin/kotlinc-jvm

WORKDIR /home/app

RUN gradle init \
    && mkdir -p src/main/kotlin \
    && ./gradlew build

COPY build.gradle ./build.gradle
COPY index.kt ./src/main/kotlin/index.kt
COPY function ./src/main/kotlin/function
RUN ./gradlew build \
    && unzip build/distributions/app.zip

ENV fprocess="/home/app/app/bin/app"

HEALTHCHECK --interval=1s CMD [ -e /tmp/.lock ] || exit 1

CMD ["fwatchdog"]
