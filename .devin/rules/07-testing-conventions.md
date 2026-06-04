---
description: 单元测试与集成测试规范
---

# LEST 测试规范

## 1. 测试框架与工具

### 1.1 后端测试

| 工具 | 用途 | 版本 |
|------|------|------|
| JUnit 5 | 单元测试框架 | 5.x |
| Mockito | Mock 框架 | 5.x |
| Spring Test | Spring 集成测试 | 4.0.3 |
| H2 Database | 内存数据库 | 2.x |

### 1.2 前端测试

| 工具 | 用途 | 版本 |
|------|------|------|
| Vitest | 单元测试框架 | 1.x |
| Vue Test Utils | Vue 组件测试 | 2.x |
| Playwright | E2E 测试 | 1.x |

## 2. 后端测试规范

### 2.1 单元测试

**目录结构**：
```
src/test/java/com/lest/modules/{service}/
├── service/
│   └── {Service}Test.java
├── mapper/
│   └── {Mapper}Test.java
└── controller/
    └── {Controller}Test.java
```

**命名规范**：
- 测试类：`{ClassName}Test`
- 测试方法：`test{MethodName}_{Scenario}_{ExpectedResult}`

**示例**：
```java
@SpringBootTest
class ProjectServiceTest {
  
  @Autowired
  private ProjectService projectService;
  
  @MockBean
  private ProjectMapper projectMapper;
  
  @Test
  void testSelectProjectList_WithValidFilter_ReturnsFilteredList() {
    // Arrange
    Project filter = new Project();
    filter.setStatus(1);
    List<Project> expected = Arrays.asList(
      new Project(1L, "Project 1", 1),
      new Project(2L, "Project 2", 1)
    );
    when(projectMapper.selectProjectList(filter)).thenReturn(expected);
    
    // Act
    List<Project> actual = projectService.selectProjectList(filter);
    
    // Assert
    assertEquals(2, actual.size());
    assertEquals("Project 1", actual.get(0).getName());
    verify(projectMapper, times(1)).selectProjectList(filter);
  }
  
  @Test
  void testInsertProject_WithNullName_ThrowsException() {
    // Arrange
    Project project = new Project();
    project.setName(null);
    
    // Act & Assert
    assertThrows(ServiceException.class, () -> {
      projectService.insertProject(project);
    });
  }
}
```

### 2.2 集成测试

**目录结构**：
```
src/test/java/com/lest/modules/{service}/
└── integration/
    └── {Feature}IntegrationTest.java
```

**示例**：
```java
@SpringBootTest
@AutoConfigureMockMvc
class ProjectIntegrationTest {
  
  @Autowired
  private MockMvc mockMvc;
  
  @Autowired
  private ProjectService projectService;
  
  @Test
  void testCreateProjectFlow() throws Exception {
    // 1. 创建项目
    Project project = new Project();
    project.setName("Test Project");
    project.setDescription("Test Description");
    
    MvcResult result = mockMvc.perform(
      post("/project")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(project))
    )
    .andExpect(status().isOk())
    .andReturn();
    
    // 2. 验证项目已创建
    mockMvc.perform(get("/project/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.name").value("Test Project"));
  }
}
```

### 2.3 测试覆盖率

- **目标**：核心业务逻辑覆盖率 ≥ 80%
- **工具**：JaCoCo
- **检查命令**：`mvn clean test jacoco:report`

## 3. 前端测试规范

### 3.1 单元测试

**目录结构**：
```
src/
├── components/
│   ├── MyComponent.vue
│   └── __tests__/
│       └── MyComponent.spec.ts
├── api/
│   ├── project/
│   │   ├── index.ts
│   │   └── __tests__/
│   │       └── index.spec.ts
└── stores/
    ├── project.ts
    └── __tests__/
        └── project.spec.ts
```

**命名规范**：
- 测试文件：`{FileName}.spec.ts`
- 测试套件：`describe('{ComponentName}', () => { ... })`
- 测试用例：`it('should {action} when {condition}', () => { ... })`

**示例**：
```typescript
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import ProjectForm from '@/components/ProjectForm.vue';

describe('ProjectForm', () => {
  let wrapper;
  
  beforeEach(() => {
    wrapper = mount(ProjectForm);
  });
  
  it('should render form fields correctly', () => {
    expect(wrapper.find('input[name="name"]').exists()).toBe(true);
    expect(wrapper.find('textarea[name="description"]').exists()).toBe(true);
  });
  
  it('should validate required fields', async () => {
    const submitBtn = wrapper.find('button[type="submit"]');
    await submitBtn.trigger('click');
    
    expect(wrapper.vm.errors.name).toBeDefined();
  });
  
  it('should emit submit event with form data', async () => {
    await wrapper.find('input[name="name"]').setValue('Test Project');
    await wrapper.find('button[type="submit"]').trigger('click');
    
    expect(wrapper.emitted('submit')).toBeTruthy();
    expect(wrapper.emitted('submit')[0]).toEqual([
      { name: 'Test Project', description: '' }
    ]);
  });
});
```

### 3.2 API Mock

```typescript
import { vi } from 'vitest';
import * as projectApi from '@/api/project';

describe('ProjectStore', () => {
  beforeEach(() => {
    vi.spyOn(projectApi, 'pageProjects').mockResolvedValue({
      code: 200,
      msg: 'success',
      total: 2,
      rows: [
        { id: 1, name: 'Project 1' },
        { id: 2, name: 'Project 2' }
      ]
    });
  });
  
  it('should fetch projects', async () => {
    const store = useProjectStore();
    await store.fetchProjects();
    
    expect(store.projects).toHaveLength(2);
    expect(store.projects[0].name).toBe('Project 1');
  });
});
```

### 3.3 E2E 测试

**目录结构**：
```
e2e/
├── fixtures/
│   └── test-data.ts
├── pages/
│   ├── LoginPage.ts
│   └── ProjectPage.ts
└── specs/
    ├── auth.spec.ts
    └── project.spec.ts
```

**示例**：
```typescript
import { test, expect } from '@playwright/test';

test.describe('Project Management', () => {
  test.beforeEach(async ({ page }) => {
    // 登录
    await page.goto('http://localhost:5173/login');
    await page.fill('input[name="username"]', 'admin');
    await page.fill('input[name="password"]', 'admin123');
    await page.click('button[type="submit"]');
    await page.waitForURL('http://localhost:5173/project');
  });
  
  test('should create a new project', async ({ page }) => {
    // 点击新建按钮
    await page.click('button:has-text("新建项目")');
    
    // 填写表单
    await page.fill('input[name="name"]', 'Test Project');
    await page.fill('textarea[name="description"]', 'Test Description');
    
    // 提交
    await page.click('button:has-text("确定")');
    
    // 验证
    await expect(page.locator('text=Test Project')).toBeVisible();
  });
});
```

## 4. 测试执行

### 4.1 后端测试

```bash
# 运行所有测试
mvn clean test

# 运行特定测试类
mvn test -Dtest=ProjectServiceTest

# 运行特定测试方法
mvn test -Dtest=ProjectServiceTest#testSelectProjectList_WithValidFilter_ReturnsFilteredList

# 生成覆盖率报告
mvn clean test jacoco:report
# 报告位置：target/site/jacoco/index.html
```

### 4.2 前端测试

```bash
# 运行所有测试
npm run test

# 运行特定文件
npm run test -- ProjectForm.spec.ts

# 生成覆盖率报告
npm run test:coverage

# E2E 测试
npm run test:e2e
```

## 5. 测试最佳实践

### 5.1 通用原则

- **单一职责**：每个测试只测试一个功能点
- **独立性**：测试之间不应有依赖关系
- **可重复性**：测试结果应该稳定可重复
- **清晰性**：测试代码应该易于理解和维护
- **快速性**：单元测试应该快速执行（< 1s）

### 5.2 AAA 模式

```
Arrange（准备）- 设置测试数据和环境
Act（执行）- 执行被测试的代码
Assert（断言）- 验证结果
```

### 5.3 避免常见错误

```java
// ❌ 错误：测试多个功能
void testProjectCreation() {
  // 创建项目
  // 验证项目
  // 添加成员
  // 验证成员
}

// ✅ 正确：单一职责
void testProjectCreation_WithValidData_CreatesSuccessfully() { ... }
void testAddProjectMember_WithValidUser_AddsSuccessfully() { ... }

// ❌ 错误：依赖测试执行顺序
void testA() { projectService.insertProject(...); }
void testB() { projectService.selectProjectList(...); } // 依赖 testA

// ✅ 正确：每个测试独立设置数据
void testA() { 
  projectService.insertProject(...);
  // 验证
}
void testB() { 
  projectService.insertProject(...); // 重新创建
  projectService.selectProjectList(...);
  // 验证
}
```

## 6. CI/CD 集成

- 每次 Push 自动运行测试
- 测试失败则阻止 Merge
- 生成测试报告和覆盖率统计
- 定期生成趋势报告
