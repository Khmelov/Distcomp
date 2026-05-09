#!/bin/sh
set -eu
until cqlsh cassandra 9042 -e "DESCRIBE KEYSPACES" 2>/dev/null; do
  echo "Waiting for CQL..."
  sleep 3
done
cqlsh cassandra 9042 -f /init/01-keyspace.cql
