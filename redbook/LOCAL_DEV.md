# Local Development

This branch removes the Cassandra and ZooKeeper runtime requirements:

- `redbook-kv` stores note/comment content in MySQL tables `redbook_kv.note_content` and `redbook_kv.comment_content`.
- `redbook-distributed-id-generator` uses a local Snowflake `worker-id` from `leaf.properties`.
- Docker Compose starts MySQL, Redis, MinIO, and RocketMQ. Nacos is available behind an optional profile for services that still use discovery.

## Start Infrastructure

```bash
cd redbook/deploy
docker compose up -d mysql redis minio rocketmq-namesrv rocketmq-broker
```

Optional Nacos:

```bash
docker compose --profile nacos up -d nacos
```

Default local values:

- MySQL: `127.0.0.1:3306`, user `root`, password `redbook123`
- Redis: `127.0.0.1:6379`
- MinIO API: `127.0.0.1:9000`, console: `127.0.0.1:9001`, user `redbookminio`, password `redbookminio123`
- RocketMQ NameServer: `127.0.0.1:9876`
- OSS service: `127.0.0.1:8083`
- KV service: `127.0.0.1:8084`
- ID service: `127.0.0.1:8085`

## Useful Checks

```bash
curl -X POST http://127.0.0.1:8084/kv/note/content/add ^
  -H "Content-Type: application/json" ^
  -d "{\"uuid\":\"11111111-1111-1111-1111-111111111111\",\"content\":\"hello\"}"

curl -X POST http://127.0.0.1:8084/kv/note/content/find ^
  -H "Content-Type: application/json" ^
  -d "{\"uuid\":\"11111111-1111-1111-1111-111111111111\"}"

curl http://127.0.0.1:8085/id/snowflake/get/test
```
