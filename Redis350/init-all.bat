@echo off
echo Initializing Kafka Comment System...
echo.

REM 1. Start infrastructure
call start-infrastructure.bat

REM 2. Wait for Cassandra to be ready
echo Waiting for Cassandra to start...
:wait_cassandra
docker exec cassandra cqlsh -e "describe keyspaces" >nul 2>&1
if errorlevel 1 (
    echo Still waiting for Cassandra...
    timeout /t 5 /nobreak >nul
    goto wait_cassandra
)

REM 3. Initialize Cassandra schema
echo Initializing Cassandra schema...
docker cp init-cassandra.cql cassandra:/tmp/init.cql
docker exec cassandra cqlsh -f /tmp/init.cql

REM 4. Create Kafka topics manually (если auto.create.topics.enable=false)
echo Creating Kafka topics...
docker exec kafka kafka-topics --create --bootstrap-server localhost:9092 --topic InTopic --partitions 3 --replication-factor 1
docker exec kafka kafka-topics --create --bootstrap-server localhost:9092 --topic OutTopic --partitions 3 --replication-factor 1

echo.
echo ============================================
echo System initialized successfully!
echo ============================================
echo Publisher API: http://localhost:24110/api/v1.0/comments
echo Discussion API: http://localhost:24130
echo Kafka Topics:
echo   - InTopic: for comment requests
echo   - OutTopic: for moderation results
echo ============================================