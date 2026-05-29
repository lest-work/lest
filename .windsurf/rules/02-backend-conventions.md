---
description: LEST Platform 后端 Java/Spring 开发规范
---

# 后端开发规范

## 1. 基础约定

- **Java 17+**，无 Lombok，手写 getter/setter
- **原生 MyBatis + PageHelper**，禁止引入 MyBatis-Plus
- 所有 Controller 继承 `BaseController`（来自 `lest-common-core`）
- 无 `build.gradle`，统一使用 Maven（`pom.xml`）

## 2. Controller 规范

### 2.1 类注解

```java
/**
 * 模块名称
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/resource")   // 不含服务路由前缀，见网关规则
public class XxxController extends BaseController {
```

### 2.2 网关路由与 @RequestMapping 对应关系

| 所在服务 | 网关路由前缀 | Controller @RequestMapping | 前端调用示例 |
|---|---|---|---|
| lest-system | `/system/**` | `/user`、`/operlog`、`/dashboard` | `/api/system/user/list` |
| lest-project | `/project/**` | `""` (空) | `/api/project/list` |
| lest-task | `/task/**` | `""` (空) | `/api/task/list` |
| lest-job | `/job/**` | `/job`（等待修复） | `/api/job/job/list` |

> lest-project 和 lest-task 的 `@RequestMapping` 必须为 `""` 空字符串，因为网关 StripPrefix=1 已经去掉了服务前缀段。

### 2.3 方法注解

```java
// 列表查询（需权限）
@RequiresPermissions("module:resource:list")
@GetMapping("/list")
public TableDataInfo list(Xxx query) {
    startPage();
    List<Xxx> list = xxxService.selectXxxList(query);
    return getDataTable(list);
}

// 写操作（需权限 + 操作日志）
@RequiresPermissions("module:resource:add")
@Log(title = "资源名称", businessType = BusinessType.INSERT)
@PostMapping
public AjaxResult add(@RequestBody @Validated Xxx xxx) {
    return toAjax(xxxService.insertXxx(xxx));
}

// 仅认证无需权限的接口（如 dashboard 概览）
@GetMapping("/overview")
public AjaxResult overview() {
    return success(xxxService.getOverview());
}
```

### 2.4 内部调用接口

```java
// 仅允许微服务内部调用（不经过 JWT 验证）
@InnerAuth
@GetMapping("/inner/xxx")
public R<Xxx> innerGetXxx(...) { ... }
```

## 3. Service 规范

```java
public interface IXxxService {
    List<Xxx> selectXxxList(Xxx xxx);
    Xxx selectXxxById(Long id);
    int insertXxx(Xxx xxx);
    int updateXxx(Xxx xxx);
    int deleteXxxById(Long id);
    int deleteXxxByIds(Long[] ids);
}
```

## 4. Mapper 规范

- 接口文件：`mapper/XxxMapper.java`
- XML 文件：`resources/mapper/XxxMapper.xml`
- namespace = 完整接口路径
- resultMap id 命名：`XxxResult`，type = 完整类路径

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.lest.modules.xxx.mapper.XxxMapper">

    <resultMap type="com.lest.modules.xxx.domain.Xxx" id="XxxResult">
        <id     property="id"   column="id"   />
        <result property="name" column="name" />
    </resultMap>

    <sql id="selectXxxVo">
        select id, name from xxx
    </sql>

    <select id="selectXxxList" parameterType="Xxx" resultMap="XxxResult">
        <include refid="selectXxxVo"/>
        <where>
            <if test="name != null and name != ''">
                AND name like concat('%', #{name}, '%')
            </if>
        </where>
        ORDER BY id DESC
    </select>

</mapper>
```

## 5. Domain 规范

- 继承 `BaseEntity`（含 createBy/createTime/updateBy/updateTime/remark）
- 纯 Java Bean：无注解，手写所有 getter/setter
- 非数据库字段加注释 `/** 非数据库字段 */`
- 日期字段加 `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")`

```java
public class Xxx extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 名称 */
    private String name;

    /** 非数据库字段 */
    private String extraField;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ...
}
```

## 6. 响应格式

- **列表接口**：返回 `TableDataInfo`（含 `code`, `msg`, `rows`, `total`）
- **单条/操作接口**：返回 `AjaxResult`（含 `code`, `msg`, `data`）
- 成功业务码 = **200**，`msg` 字段（非 `message`）

```java
// 列表
return getDataTable(list);  // code=200, msg="查询成功", rows=[...], total=N

// 成功
return success();           // code=200, msg="操作成功"
return success(data);       // code=200, msg="操作成功", data=...

// 操作结果（影响行数）
return toAjax(rows);        // rows>0 → code=200, else code=500

// 错误
return error("xxx失败");   // code=500, msg="xxx失败"
```

## 7. application.yml 规范

```yaml
spring:
  application:
    name: lest-xxx
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:127.0.0.1}:3306/${MYSQL_DB:lest_platform}
    # ...

mybatis:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.lest.modules.xxx.domain
  configuration:
    map-underscore-to-camel-case: true
```

注意：使用 `mybatis:` 而非 `mybatis-plus:`。

## 8. 权限字符串命名规范

格式：`{服务}:{资源}:{动作}`

| 动作 | 含义 |
|---|---|
| `list` | 查询列表 |
| `query` | 查询详情 |
| `add` | 新增 |
| `edit` | 修改 |
| `remove` | 删除 |
| `export` | 导出 |

示例：`system:user:list`、`project:project:add`、`task:task:edit`
