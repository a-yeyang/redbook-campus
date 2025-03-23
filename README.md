# Redbook Campus 🎓

**研究生科研学习与生活社区平台**  
基于小红书设计理念打造的高并发学术社交平台，支持千万级用户实时互动，提供科研交流、资源共享、社交连接等核心功能。

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen)](https://spring.io)
[![Nacos](https://img.shields.io/badge/Nacos-2.2.3-critical)](https://nacos.io)

---

## 🌐 平台概览

### 核心功能矩阵
| 功能模块       | 能力描述                           | 技术亮点                              |
|----------------|----------------------------------|-------------------------------------|
| **笔记系统**   | 图文/视频发布、多级缓存、热点置顶      | RocketMQ广播缓存同步 + CompletableFuture并发优化 |
| **社交关系**   | 关注/粉丝管理、动态推送              | Redis ZSET有序存储 + Lua脚本原子操作         |
| **计数服务**   | 实时点赞/收藏统计                   | BufferTrigger流量聚合 + 分片补偿对齐        |
| **搜索系统**   | 多维度笔记检索                      | Elasticsearch + Canal增量同步         |
| **对象存储**   | 图片/视频云端管理                   | 多存储源动态路由 + Minio集群部署           |

---

## 🏗️ 架构全景

### 分布式服务体系
```mermaid
graph TD
    G[API Gateway] --> A[Auth Service]
    G --> U[User Service]
    G --> N[Note Service]
    G --> C[Comment Service]
    G --> S[Search Service]
    N --> MQ[[RocketMQ]]
    MQ --> CT[Counter Service]
    MQ --> ES[Elasticsearch]
    U --> R[Redis Cluster]


集群部署：Nacos集群 + Redis哨兵模式 + Minio分布式存储
