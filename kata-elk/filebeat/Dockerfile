FROM baseos:latest

RUN curl -L -O https://download.elastic.co/beats/filebeat/filebeat-1.1.0-x86_64.rpm \
&&  rpm -vi filebeat-1.1.0-x86_64.rpm
COPY filebeat.yml /filebeat.yml
RUN chmod -v 754 /filebeat.yml
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod -v 754 /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
