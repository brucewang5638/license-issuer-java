package com.example.licenseissuer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class LicenseManager {
    private static final String ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String PRIVATE_KEY_FILE = "private_key.pem";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void generateLicense(com.example.licenseissuer.LicenseData data, File outputFile) throws Exception {
        // 获取私钥
        PrivateKey privateKey = getPrivateKey();

        // 创建证书JSON结构
        Map<String, Object> certificate = new HashMap<>();

        // metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("kid", data.getKid());
        metadata.put("notBefore", data.getNotBefore());
        metadata.put("notAfter", data.getNotAfter());
        metadata.put("serialNumber", data.getSerialNumber());
        certificate.put("metadata", metadata);

        // payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("customerName", data.getCustomerName());
        payload.put("licenseType", data.getLicenseType());
        payload.put("machineId", data.getMachineId());
        payload.put("macs", data.getMacs());
        certificate.put("payload", payload);

        // 对payload进行签名
        String payloadJson = gson.toJson(payload);
        String signature = signData(payloadJson, privateKey);
        certificate.put("signature", signature);

        // 保存证书文件
        try (FileWriter writer = new FileWriter(outputFile)) {
            gson.toJson(certificate, writer);
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