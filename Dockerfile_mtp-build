
FROM frekele/java:jdk8

ENV LOCAL_SRC_HOME .
ENV SRC_HOME /code/mtp

RUN apt-get update

RUN apt-get -y install git

ENV NODE_VERSION 0.12.0
ENV NODE_DIR /opt/nodejs

RUN mkdir ${NODE_DIR} && \
	curl -L https://nodejs.org/dist/v${NODE_VERSION}/node-v${NODE_VERSION}-linux-x64.tar.gz | tar xvzf - -C ${NODE_DIR} --strip-components=1

ENV PATH $PATH:${NODE_DIR}/bin

RUN ["npm", "cache", "clean", "-f"]
RUN ["npm", "install", "bower", "-g"]
RUN ["npm", "install", "grunt-cli", "-g"]

WORKDIR $SRC_HOME

ADD $LOCAL_SRC_HOME $SRC_HOME

WORKDIR $SRC_HOME/mtp-frontend

RUN ["npm", "--no-optional", "install"]

RUN ["bower", "--allow-root", "install"]

RUN ["grunt", "build"]

WORKDIR $SRC_HOME
RUN ["./gradlew", "clean", "distZip"]


