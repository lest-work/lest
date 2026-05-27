# LEST Platform 前端项目结构

## 目录说明

```
frontend-pc/     # PC端管理后台（基于 Vue 3 + Element Plus + ele-admin-plus）
├── src/
│   ├── api/           # API 接口定义
│   ├── assets/         # 静态资源
│   ├── components/     # 公共组件
│   ├── config/        # 配置文件
│   ├── i18n/          # 国际化
│   ├── layout/         # 布局组件
│   ├── router/        # 路由配置
│   ├── store/         # 状态管理
│   ├── styles/        # 全局样式
│   ├── utils/         # 工具函数
│   └── views/         # 页面视图
│       ├── system/     # 系统管理
│       │   ├── user/           # 用户管理
│       │   ├── role/           # 角色管理
│       │   ├── menu/           # 菜单管理
│       │   ├── organization/   # 机构管理
│       │   └── dictionary/     # 字典管理
│       ├── wakapi/     # WakaTime 集成
│       ├── user/       # 用户中心
│       └── login/      # 登录页面
├── public/             # 公共资源
├── package.json        # 项目配置
├── vite.config.ts     # Vite 配置
└── tsconfig.json      # TypeScript 配置

frontend-h5/     # H5 移动端（待开发）
├── src/
│   ├── api/           # API 接口定义
│   ├── assets/        # 静态资源
│   ├── components/    # 公共组件
│   ├── pages/         # 页面
│   ├── router/        # 路由配置
│   ├── store/         # 状态管理
│   ├── utils/         # 工具函数
│   └── App.vue        # 根组件
├── public/             # 公共资源
├── package.json        # 项目配置
└── vite.config.ts     # Vite 配置

frontend-app/    # APP 端（待开发，React Native 或 Flutter）
├── src/
│   ├── api/
│   ├── components/
│   ├── screens/
│   ├── navigation/
│   ├── services/
│   ├── store/
│   ├── utils/
│   └── App.tsx
├── android/            # Android 原生代码
├── ios/                # iOS 原生代码
├── package.json
└── app.json
```

## 技术栈

### PC 端 (frontend-pc)
- **框架**: Vue 3.5+ (Composition API)
- **构建工具**: Vite 6.x
- **UI 组件**: Element Plus 2.x + ele-admin-plus
- **状态管理**: Pinia 3.x
- **路由**: Vue Router 4.x
- **类型**: TypeScript 5.x
- **样式**: SCSS
- **HTTP**: Axios

### H5 端 (frontend-h5)
- **框架**: Vue 3 (Composition API) 或 React
- **构建工具**: Vite 或 Uni-app
- **UI 组件**: Vant 或 Element Plus Mobile
- **状态管理**: Pinia 或 Zustand

### APP 端 (frontend-app)
- **框架**: React Native 或 Flutter
- **状态管理**: Redux 或 Riverpod

## 开发指南

### 安装依赖
```bash
cd frontend-pc
npm install
```

### 开发模式
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 代码规范
- 使用 ESLint + Prettier 进行代码检查
- 遵循 Vue 3 Composition API 规范
- 使用 TypeScript 进行类型约束
- 组件命名使用 PascalCase
- 样式使用 SCSS，预处理器变量统一管理

## 文档链接
- [PRD 文档](../docs/PRD/V1.0/)
- [API 文档](../docs/API.md)
- [数据库设计](../docs/DATABASE.md)
- [架构设计](../docs/ARCHITECTURE.md)
- [开发指南](../docs/DEVELOPMENT.md)
