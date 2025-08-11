package com.example.licenseissuer;

import com.example.licenseissuer.util.KeyStoreManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LicenseManager {
    private static final String ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String PRIVATE_KEY_FILE = "private_key.pem";
    private final KeyStoreManager keyStoreManager;

    public LicenseManager(KeyStoreManager keyStoreManager) {
        this.keyStoreManager = keyStoreManager;
    }

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void generateLicense(LicenseData data, File outputFile) throws Exception {
        // 获取私钥
        PrivateKey privateKey = keyStoreManager.getPrivateKey(data.getKid());
        if (privateKey == null) {
            throw new IllegalStateException("无法从密钥库中找到 kid=" + data.getKid() + " 对应的私钥");
        }

        // 创建证书JSON结构
        Map<String, Object> certificate = new HashMap<>();

        // metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("kid", data.getKid());
        metadata.put("notBefore", data.getNotBefore());
        metadata.put("notAfter", data.getNotAfter());
        metadata.put("serialNumber", data.getSerialNumber());

        // payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("customerName", data.getCustomerName());
        payload.put("licenseType", data.getLicenseType());
        payload.put("machineId", data.getBoardSerial());
        payload.put("macs", data.getMacs());

        Map<String, Object> fullData = new LinkedHashMap<>();
        fullData.put("metadata", metadata);
        fullData.put("payload", payload);

        // 转成 JSON 并 Base64 编码
        String jsonData = gson.toJson(fullData);
        String base64Data = Base64.getEncoder().encodeToString(jsonData.getBytes(StandardCharsets.UTF_8));

        // 签名
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(base64Data.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(sig.sign());

        // 最终 license 文件结构
        Map<String, Object> licenseFile = new HashMap<>();
        licenseFile.put("kid", data.getKid());
        licenseFile.put("data", base64Data);
        licenseFile.put("signature", signature);

        // 保存
        try (FileWriter writer = new FileWriter(outputFile)) {
            gson.toJson(licenseFile, writer);
        }
    }

    private PrivateKey getPrivateKey() throws Exception {
        File keyFile = new File(PRIVATE_KEY_FILE);
        if (!keyFile.exists()) {
            // 生成新的密钥对
            generateKeyPair();
        }

        String privateKeyPEM = new String(Files.readAllBytes(keyFile.toPath()));
        // 移除PEM头尾和换行符
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    private void generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // 保存私钥
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        String privateKeyPEM = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getEncoder().encodeToString(privateKeyBytes) + "\n" +
                "-----END PRIVATE KEY-----";
        Files.write(new File(PRIVATE_KEY_FILE).toPath(), privateKeyPEM.getBytes());

        // 保存公钥（用于Java代码）
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        String publicKeyPEM = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getEncoder().encodeToString(publicKeyBytes) + "\n" +
                "-----END PUBLIC KEY-----";
        Files.write(new File("public_key.pem").toPath(), publicKeyPEM.getBytes());

        System.out.println("新密钥对已生成:");
        System.out.println("私钥: " + PRIVATE_KEY_FILE);
        System.out.println("公钥: public_key.pem");
    }

    private String signData(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data.getBytes("UTF-8"));
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
}