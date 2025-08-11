package com.example.licenseissuer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignatureVerificationTest {

    @Test
    public void testSignatureVerification() throws Exception {
        // 1. 读取 license 文件
        File licenseFile = Paths.get("src/test/resources/license.license").toFile();
        JsonObject obj = JsonParser.parseReader(new FileReader(licenseFile)).getAsJsonObject();
        String base64Data = obj.get("data").getAsString();
        String signature = obj.get("signature").getAsString();

        // 3. 加载公钥
        PublicKey publicKey= loadPublicKey("src/test/resources/key_public.der");
        // 校验签名
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(base64Data.getBytes(StandardCharsets.UTF_8));

        boolean valid = sig.verify(Base64.getDecoder().decode(signature));

        if (valid) {
            System.out.println("✅ 签名验证成功！");
            String jsonData = new String(Base64.getDecoder().decode(base64Data), StandardCharsets.UTF_8);
            System.out.println("解码后的内容：" + jsonData);
        } else {
            throw new Exception("❌ 签名验证失败！");
        }
    }


    private PublicKey loadPublicKey(String filePath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filePath));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }
}
