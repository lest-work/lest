# LEST Platform Native APP

## 项目介绍

LEST Platform 的原生 APP，为 iOS 和 Android 用户提供原生体验。

## 技术栈

- **框架**: React Native 0.79+
- **语言**: TypeScript
- **导航**: React Navigation 7.x
- **状态管理**: Zustand 5.x
- **HTTP**: Axios

## 功能模块

- [ ] 登录/注册
- [ ] 首页仪表盘
- [ ] 任务管理
- [ ] 看板视图
- [ ] 通知消息推送
- [ ] 个人中心
- [ ] 扫码功能
- [ ] 拍照上传

## 开发指南

### 环境要求
- Node.js 18+
- JDK 17+
- Xcode 15+ (iOS 开发)
- Android Studio / Android SDK (Android 开发)

### 安装依赖
```bash
npm install
cd ios && pod install
```

### 运行应用
```bash
# iOS
npm run ios

# Android
npm run android
```

### 构建发布
```bash
# Android
npm run build:android

# iOS
npm run build:ios
```

## 文档链接

- [主项目文档](../docs/README.md)
- [API 文档](../docs/reference/api/API.md)
