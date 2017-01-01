
FROM mtp-build:0.5

ARG MTP_MODULE=

ENV MTP_MODULE_ENV $MTP_MODULE
ENV SRC_HOME /code/mtp
ENV MTP_VERSION 0.5.0
ENV WORK_HOME /opt/mtp


WORKDIR /usr/local/bin
ENV DOCKERIZE_VERSION v0.3.0
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz


WORKDIR $WORK_HOME

RUN unzip $SRC_HOME/$MTP_MODULE/build/distributions/$MTP_MODULE-$MTP_VERSION.zip

WORKDIR $WORK_HOME/$MTP_MODULE-$MTP_VERSION

COPY conf/spring-application_$MTP_MODULE.yml conf/spring-application.yml

CMD dockerize -wait tcp://mtp-data:9042 -timeout 60s $WORK_HOME/$MTP_MODULE_ENV-$MTP_VERSION/bin/$MTP_MODULE_ENV --spring.profiles.active=prod
