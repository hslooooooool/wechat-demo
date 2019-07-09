# 安卓快速开发框架 [ABCL](https://github.com/hslooooooool/abcl)

## 项目介绍

### 前言
本项目为自贡情指勤项目安卓客户端项目代码

为避免过度设计、设计缺陷等问题，暂定框架的开发边界仅为安卓核心功能，如需要常用工具、自定义控件请自行拓展。

### 结构：

![结构图](doc/app-packge.png)

### 技术特征

- Android Jetpack(AndroidX)
- Lifecycles/LiveData/ViewModel/Room/Paging
- Android KTX/Kotlin
- RxJava/RxAndroid
- Retrofit2/OKHttp3
- ARouter

### 使用成本

- Android Studio 3.4+
- Gradle 5.1.1+
- Kotlin 1.3.31+
- CompileSdkVersion 28+

### 开发约定

- lib层仅维护实体对象，资源、路由、常量
- 独有功能在base和core间增加并在base-core中依赖
- 开发满足[开发规范](https://github.com/hslooooooool/dev-doc/)

### 各功能依赖

- 【进行中】APP[app]华清松

- 【进行中】组合业务-消息 [base-message]华清松
- 【进行中】组合业务-工作台 [base-work]华清松
- 【进行中】组合业务-我的 [base-mine]华清松
- 【进行中】独立业务-聊天 [base-chat]华清松
    - 【进行中】聊天常用语模板——华清松
- 【进行中】独立业务-指令详情 [base-order]华清松
    - 【进行中】指令签收弹窗——华清松
    
- 【已完成】核心功能依赖[base-core]华清松

- 【进行中】核心功能-消息通信 [core-socket]华清松
- 【未开始】核心功能-记事本 [core-netbook]曹星
- 【进行中】核心功能-公安地图展示、位置选择、导航 [core-map]曹星
- 【进行中】核心功能-GPS定位 [core-location]曹星
- 【进行中】核心功能-应用保活 [core-keepalive]华清松
- 【进行中】核心功能-身份识别、人脸识别 [core-nfc]曹星
- 【已完成】核心功能-二维码 [core-qrcode]华清松
- 【已完成】核心功能-动态表单 [core-form]华清松
- 【已完成】核心功能-拍照、图库选择、文件选择、视频录制、音频录制 [core-file]华清松
- 【已完成】核心功能-视频播放、图片画廊 [core-play]华清松
    - 【已完成】图片预览（放大缩小，上下切换）——华清松

- 【已完成】基础功能依赖[core-lib]华清松

- 【已完成】基础功能-网络请求 [lib-netservice]华清松
- 【已完成】基础功能-基础框架（基础配置、路由管理、数据库管理、常量管理、工具类） [lib-base]华清松

## keytool -list -v -keystore C:\Users\admin\AndroidStudioProjects\ydqzq\doc\release.jks