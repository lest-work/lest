# LEST Platform 开发规范细节补充文档 (DEVELOPMENT_SPEC_ENHANCEMENT.md)

为了确保后续开发和 AI 开发者在编写代码时能够严格遵守 LEST Platform 的工程结构和质量要求，特制定本开发细节规范补充文档。

---

## 1. Gradle 依赖管理树规范

LEST Platform 采用 Gradle Monorepo 机制进行模块化开发。所有公共组件都下沉在 `lest-common` 模块，严禁在子模块之间建立直接的、未经授权的交叉引用。

### 1.1 依赖架构图

```
lest-platform (根项目)
  ├── backend (Gradle Monorepo)
  │     ├── build.gradle (根构建配置：定义 Spring Boot 4.0.6, Cloud BOM, Java 25 版本及全局仓库配置)
  │     ├── settings.gradle (子模块注册列表)
  │     │
  │     ├── lest-common (公共基础库，仅打包为 JAR 不作为启动应用)
  │     │     ├── lest-common-log (日志组件：统一 Logback 格式、MDC 链路 ID)
  │     │     ├── lest-common-redis (Redis 组件：Jackson 序列化器、分布式锁)
  │     │     └── lest-common-security (安全组件：JWT 令牌解析、Security 过滤器、权限拦截器)
  │     │
  │     └── lest-modules (业务微服务，可单独构建为 BootJar 并部署)
  │           ├── lest-gateway (API 网关：依赖 lest-common-security 传递 JWT，但不依赖其他业务服务)
  │           ├── lest-auth (认证服务：依赖 lest-common-security 与 lest-common-redis)
  │           └── lest-task (任务服务：依赖 lest-common-security, lest-common-redis 以及 lest-common-log)
```

### 1.2 根 `build.gradle` 最佳实践（依赖版本锁定）

所有子模块的依赖版本统一由根 `build.gradle` 中的 `dependencyManagement` 进行声明，子模块内部严禁硬编码版本号：

```groovy
// backend/build.gradle 示例
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.0.6' apply false
    id 'io.spring.dependency-management' version '1.1.7' apply false
}

ext {
    set('springCloudVersion', '2025.0.2')
    set('springBootVersion', '4.0.6')
    set('javaVersion', '25')
}

subprojects {
    apply plugin: 'java'
    apply plugin: 'io.spring.dependency-management'

    java {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    dependencyManagement {
        imports {
            mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
            mavenBom "org.springframework.boot:spring-boot-dependencies:${springBootVersion}"
        }
    }
}
```

### 1.3 业务子模块 `build.gradle` 编写范式

以 `lest-task` 模块为例，仅引入自身业务所需的包和公共依赖组件：

```groovy
// backend/lest-modules/lest-task/build.gradle
plugins {
    id 'org.springframework.boot'
    id 'io.spring.dependency-management'
}

dependencies {
    // 1. 引入本模块特定的公共组件（严禁引用其他 lest-modules 子模块）
    implementation project(':lest-common:lest-common-security')
    implementation project(':lest-common:lest-common-log')
    implementation project(':lest-common:lest-common-redis')

    // 2. 引入 Spring Boot Starter (版本号由根 BOM 自动解析管理)
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation 'com.baomidou:mybatis-plus-spring-boot3-starter:3.5.16'

    // 3. 开发辅助工具与测试
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

---

## 2. Flyway 脚本存放与执行规范

LEST Platform 微服务采用独立的数据库架构。为了保证数据库版本迁移的严密性和 AI 开发的可追溯性，所有 Flyway 脚本必须严格按照如下规范存放。

### 2.1 脚本目录结构

每个微服务在自身的 `src/main/resources` 目录下设置专门的 migration 路径，严禁将所有模块的 SQL 脚本堆放在一个目录下：

```
lest-task
  └── src
        └── main
              └── resources
                    └── db
                          └── migration
                                └── task (特定服务专属子目录)
                                      ├── V1.0.0__init_task_tables.sql
                                      ├── V1.1.0__add_task_priority.sql
                                      └── V1.1.1__create_worklog_index.sql
```

### 2.2 命名与书写规则

1. **版本号格式**：`V{大版本}.{小版本}.{补丁号}__{简短英文业务描述}.sql`。
   * 注意：版本号与业务描述之间必须有**双下划线** `__` 分隔。
2. **幂等性原则**：所有 DDL/DML 必须考虑重复执行的可能性（使用 `CREATE TABLE IF NOT EXISTS`、`ALTER TABLE ... ADD COLUMN ...` 前检查是否已存在等，或严格保证每个文件仅执行一次且通过 Flyway 记录版本）。
3. **配置文件（以 lest-task 的 application-dev.yml 为例）**：
   ```yaml
   spring:
     flyway:
       enabled: true
       clean-disabled: true # 严禁在生产和测试环境执行 clean 操作
       locations: classpath:db/migration/task
       table: flyway_schema_history_task # 独立每个服务的版本表
   ```

---

## 3. 测试环境 Mock 最佳实践规范

由于分布式微服务强依赖外部网络接口（如 OpenFeign 调用）和中间件（如 Kafka），未经 Mock 的测试会导致单元测试无法在 CI 流水线中跑通。

### 3.1 Feign 客户端的 Mocking 规范

当测试 `lest-task` 业务层且该业务调用了 `AuthClient` 时，必须使用 `@MockBean` 将 FeignClient 代理掉，严禁发起真实的 HTTP 调用。

```java
package com.lest.task.service;

import com.lest.common.core.domain.Result;
import com.lest.task.client.AuthClient;
import com.lest.task.domain.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    // Mock 外部 RPC Client，切断外部网络依赖
    @MockBean
    private AuthClient authClient;

    @Test
    public void testCreateTaskWithAssignee() {
        // 1. 定义 Mock 返回
        Long mockUserId = 999L;
        UserVO mockUser = new UserVO(mockUserId, "MockUser", "DEVELOPER");
        Mockito.when(authClient.getUserById(mockUserId))
               .thenReturn(Result.ok(mockUser));

        // 2. 执行测试逻辑
        boolean result = taskService.createTask(mockUserId, "实现任务系统 Mock 测试");

        // 3. 验证
        assertEquals(true, result);
        Mockito.verify(authClient, Mockito.times(1)).getUserById(mockUserId);
    }
}
```

### 3.2 Kafka 事件发送与消费的 Mocking 规范

微服务之间的解耦主要通过 Kafka 异步事件。在集成测试中，推荐使用 `spring-kafka-test` 的内置嵌入式 Kafka（Embedded Kafka）或对事件发布器 `LestEventPublisher` 进行 Mock（推荐，最轻量）。

#### 方案一：对发布接口 `LestEventPublisher` 进行 @MockBean

```java
@SpringBootTest
public class TaskCompletedEventTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private LestEventPublisher eventPublisher;

    @Test
    public void testCompleteTaskPublishesEvent() {
        // 1. 执行完成任务逻辑
        Long taskId = 10086L;
        taskService.completeTask(taskId);

        // 2. 验证是否向正确的主题发送了事件通知
        Mockito.verify(eventPublisher, Mockito.times(1))
               .publish(Mockito.eq("lest-dev-task-taskCompleted"), Mockito.any());
    }
}
```

#### 方案二：集成测试中引入 `@EmbeddedKafka`（适合验证完整的监听消费逻辑）

```java
package com.lest.task.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class TaskEventIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private TaskEventConsumer consumer; // 监听器 Bean

    @Test
    public void testConsumeTaskCreatedEvent() throws Exception {
        String payload = "{\"specversion\":\"1.0\",\"type\":\"com.lest.task.taskCreated\",\"data\":{\"taskId\":101}}";
        
        // 发送消息到本地嵌合 Kafka
        kafkaTemplate.send("lest-dev-task-taskCreated", payload);

        // 等待消费者异步处理并验证断言
        boolean consumed = consumer.getLatch().await(5, TimeUnit.SECONDS);
        assertTrue(consumed);
    }
}
```
