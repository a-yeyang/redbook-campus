# Redbook Campus 🎓

**仿小红书架构的研究生科研社区平台**  
基于微服务分布式架构的高并发学术社交系统，支持千万级用户实时互动，提供科研交流、资源共享、社交连接等核心场景解决方案。

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen)](https://spring.io)
![Nacos](https://img.shields.io/badge/Nacos-2.2.3-critical)

---

## 🌟 核心功能

| 模块                | 功能描述                                                                 | 技术亮点                                                                 |
|---------------------|------------------------------------------------------------------------|-------------------------------------------------------------------------|
| **智能认证体系**    | 多方式登录/权限控制/安全审计                                           | SaToken JWT鉴权 + RBAC模型 + 阿里云SMS熔断防护                            |
| **笔记生态系统**    | 多模态发布/热点置顶/分级缓存                                           | RocketMQ广播同步 + CompletableFuture并发优化 + 多级缓存架构                |
| **社交关系引擎**    | 关注取关/动态推送/粉丝分析                                             | Redis ZSET有序存储 + Lua脚本原子化 + MQ顺序消息                            |
| **智能计数系统**    | 实时统计/流量聚合/数据对齐                                             | BufferTrigger批处理 + 分片补偿任务 + 最终一致性保障                        |
| **混合存储方案**    | 结构化/非结构化数据统一管理                                            | MySQL分库分表 + Cassandra短文本存储 + Minio多云路由                        |

---

## 🏗 技术全景

### 分布式架构拓扑
```mermaid
graph TD
    G[API Gateway] -->|路由| A[Auth Service]
    G -->|负载均衡| U[User Service]
    G -->|熔断| N[Note Service]
    G -->|降级| C[Comment Service]
    G -->|限流| S[Search Service]
    N -->|事务消息| MQ[[RocketMQ Cluster]]
    MQ -->|削峰填谷| CT[Counter Service]
    MQ -->|增量同步| ES[Elasticsearch]
    U -->|二级缓存| RC[Redis Cluster]
