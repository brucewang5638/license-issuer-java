# 🔐 软件授权管理系统 - 完整使用指南

## 📋 系统概述

该授权管理系统提供完整的软件许可证签发和验证解决方案，包含：
- **JavaFX桌面程序**: 用于签发授权证书
- **Spring Boot验证模块**: 集成到Java应用中验证授权

## 🛠 技术选型说明

### 加密算法: RSA 2048位
- **选择原因**: JDK 8原生支持，兼容性好，安全性成熟
- **签名算法**: SHA256withRSA
- **密钥管理**: 私钥签发，公钥验证

### 硬件绑定策略
- **机器ID**: CPU序列号 + 主板序列号 + BIOS序列号的SHA256哈希
- **MAC地址**: 支持多网卡，任一匹配即可
- **容错机制**: 硬件更换视为新设备，需重新授权

## 🚀 快速开始

### 第一步: 构建发行方程序

```bash
# 进入发行方程序目录
cd license-issuer

# Maven构建
mvn clean package

# 运行程序 (需要JDK 8+)
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/license-issuer-1.0.0.jar

# 或使用Maven插件运行
mvn javafx:run
```

### 第二步: 获取目标机器硬件信息

在目标服务器上运行硬件信息收集工具:

```bash
# 编译Spring Boot项目
mvn clean compile

# 运行硬件信息收集器
mvn exec:java -Dexec.mainClass="com.example.framework.HardwareInfoCollector"
```

输出示例:
```
========== 机器硬件信息 ==========
机器ID: a1b2c3d4e5f6789012345678901234567890abcdef1234567890abcdef123456
MAC地址列表:
  00:1a:2b:3c:4d:5e
  00:1a:2b:3c:4d:5f
================================
```

### 第三步: 使用发行方程序签发证书

1. **启动JavaFX程序**
2. **填写授权信息**:
   - 客户名称: `山东宏科信息科技有限公司`
   - 授权类型: `enterprise`
   - 生效时间: `2025-01-01`
   - 到期时间: `2026-01-01`
   - 机器ID: 从第二步获取
   - MAC地址: 从第二步获取（每行一个）

3. **点击"生成证书"**，选择保存路径

### 第四步: 部署证书到目标服务器

```bash
# 将生成的证书文件放到Spring Boot项目根目录
cp /path/to/generated/license.license ./license.license

# 更新公钥 (从发行方程序目录复制)
# 将public_key.pem中的公钥内容复制到LicenseValidator.java的PUBLIC_KEY常量中
```

### 第五步: 启动受保护的应用

```bash
# 启动Spring Boot应用
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/license-framework-1.0.0.jar
```

## 📁 项目结构

```
license-system/
├── license-issuer/                 # 发行方JavaFX程序
│   ├── src/main/java/
│   │   └── com/example/licenseissuer/
│   │       ├── LicenseIssuerApp.java
│   │       ├── LicenseData.java
│   │       └── LicenseManager.java
│   ├── src/main/resources/
│   │   └── styles.css
│   └── pom.xml
│
├── license-framework/              # Spring Boot验证模块
│   ├── src/main/java/
│   │   └── com/example/framework/
│   │       ├── LicenseValidatorApplication.java
│   │       ├── LicenseValidator.java
│   │       └── HardwareInfoCollector.java
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
│
├── keys/                          # 密钥文件 (自动生成)
│   ├── private_key.pem           # 私钥 (发行方保管)
│   └── public_key.pem            # 公钥 (集成到代码中)
│
└── certificates/                  # 生成的证书文件
    └── *.license
```

## 🔧 高级配置

### 自定义公钥集成

1. 运行发行方程序会自动生成`public_key.pem`
2. 提取Base64编码的公钥内容:

```bash
# 移除PEM头尾，获取Base64内容
cat public_key.pem | grep -v "BEGIN\|END" | tr -d '\n'
```

3. 更新`LicenseValidator.java`中的`PUBLIC_KEY`常量

### 运行时证书验证配置

在`application.yml`中配置验证策略:

```yaml
license:
  validation:
    enabled: true
    strict-mode: true        #
