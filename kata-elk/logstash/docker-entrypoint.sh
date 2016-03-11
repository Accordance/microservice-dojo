#!/bin/sh
set -e

# Render config file
cat logstash.conf | sed "s/ELASTICSEARCH_HOST/$ELASTICSEARCH_HOST/" | sed "s/ELASTICSEARCH_PORT/$ELASTICSEARCH_PORT/"  > logstash.tmp
cat logstash.tmp > logstash.conf
rm logstash.tmp
exec "$@"
