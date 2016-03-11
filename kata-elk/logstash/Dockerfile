FROM logstash:2.1.1
COPY logstash.conf /logstash.conf
RUN chmod -v 754 /logstash.conf
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod -v 754 /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
