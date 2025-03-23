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
技术组件矩阵
领域	关键技术栈
服务治理	Nacos 2.x + OpenFeign + Spring Cloud Gateway
数据存储	MySQL 8.0（分库分表） + Cassandra 4.x + Redis 7.x
高可用方案	RocketMQ 5.x + Hystrix + Sentinel
安全体系	SaToken + JWT + RBAC模型 + 阿里云SMS
运维监控	Prometheus + Grafana + SkyWalking 9.x
🚀 核心优势
高性能设计
三级缓存体系：Caffeine本地缓存 + Redis分布式缓存 + 数据库防穿透保护

异步削峰方案：MQ顺序消息 + 令牌桶限流 + 批量聚合写入

智能ID服务：美团Leaf号段模式（19亿/日生成能力） + 雪花算法

高可用保障
服务熔断：异常流量自动降级，核心业务线程池隔离

数据最终一致：MQ事务消息 + 分片补偿任务 + 增量对齐机制

集群部署：Nacos集群 + Redis哨兵模式 + Minio分布式存储
