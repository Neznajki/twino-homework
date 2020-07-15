FROM openjdk:14-alpine

RUN apk add --update maven &&\
    apk add --update sudo

COPY run.sh /usr/local/bin/run.sh
COPY initConfigs.sh /usr/local/bin/initConfigs.sh
COPY addUser.sh /usr/local/bin/addUser.sh

RUN chmod 0777 /usr/local/bin/run.sh /usr/local/bin/initConfigs.sh /usr/local/bin/addUser.sh

RUN head -c 5 /dev/random > random_bytes && chmod 0777 /etc/passwd

WORKDIR /project

#run.sh the spring boot application
ENTRYPOINT ["sh", "/usr/local/bin/run.sh"]