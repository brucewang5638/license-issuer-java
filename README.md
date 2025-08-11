# LicenseIssuer

LicenseIssuer 是一个桌面应用，用于生成和签发软件授权证书（license 文件），支持公钥/私钥签名机制，确保授权文件不可篡改。

## 功能特点
- 支持 **RSA 非对称加密签名**
- 提供 GUI 表单填写授权信息
- 自动生成 `.license` 授权文件
- 支持 Portable（绿色免安装版）和安装包版本（.msi / .exe）
- 跨平台构建：Windows / Linux / macOS

---

## 使用说明

### 1. 运行方式
- **Windows 安装版**  
  运行 `LicenseIssuerAppInstaller-1.0.0.exe` 或 `LicenseIssuerAppInstaller-1.0.0.msi`，按提示安装后启动。

- **Portable 绿色版**  
  解压 `LicenseIssuerApp-Portable.zip`，直接运行 `LicenseIssuerApp.exe` 即可。

### 2. 生成 License
1. 打开软件  
2. 填写授权信息（客户名称、主板 ID、MAC 地址等）  
3. 点击 "生成授权" 按钮，选择保存位置  
4. 得到 `.license` 文件，发给目标客户使用

---

## License 文件格式
软件生成的 `.license` 文件经过私钥签名，结构示例：

```json
{
  "kid": "key-2025-01",
  "data": "base64编码的授权数据",
  "signature": "base64编码的签名"
}
````

验证端使用公钥对 `data` 和 `signature` 进行校验，确保内容未被篡改。

---

## 开发与构建

### 构建命令（使用 jpackage）

```bash
./gradlew clean build jpackage
```

### GitHub Actions 自动构建

构建完成后，GitHub Actions 会自动打包并发布到 Release：

* `LicenseIssuerAppInstaller-1.0.0.msi`（Windows 安装包）
* `LicenseIssuerAppInstaller-1.0.0.exe`（Windows 安装包）
* `LicenseIssuerApp-Portable.zip`（Portable 绿色版）

---

## 公钥/私钥管理

私钥由 KeyStoreManager 自动生成，保存在安全位置；公钥可分发给验证端进行授权校验。

---

## License

本项目使用 MIT 协议，详情见 [LICENSE](LICENSE)。

